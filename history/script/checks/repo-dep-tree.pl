#!/usr/bin/perl

# read in list of repositories (in order)

# for each repo
  # get list of dependencies in repo, save in hashmap

# for each repo
  # for each pom.xml in repo
    # scan pom.xml for deps
    # for each dep found
      # check if dep is in repo-deps
      # if yes, 
        # add relationship: this-repo -> dep-repo (list: dep)

use strict;
use warnings;

use Getopt::Std;
use Cwd             qw(abs_path);
use File::Basename  qw( dirname);
use File::Find;
use XML::Simple;
use Data::Dumper;

my %opts;
getopts( 'hdfwvt:', \%opts );

my $print_deps = 1;
my $build_target;
if( exists $opts{t} ) {
  --$print_deps;
  $build_target = $opts{t};
}

my $verbose = 0;
if( exists $opts{v} ) {
  ++$verbose;
}

my $filter_transitive = 0;
if( exists $opts{f} ) {
  ++$filter_transitive;
}

my $create_dot_file = 0;
if( exists $opts{d} ) {
  ++$create_dot_file;
}

if( exists $opts{h} ) {
  print STDERR "\n";
  print STDERR " h ........ print [h]elp (this)\n";
  print STDERR " d ........ create [d]ot file (implies -f)\n";
  print STDERR "            use with -t to highlight target repository dependencies in the graph\n";
  print STDERR " f ........ [f]ilter transitive dependencies (not compatible with -t)\n";
  print STDERR " w ........ [w]arn instead of dying on invalid module versions\n";
  print STDERR " v ........ [v]erbose: show which modules are referenced\n";
  print STDERR " t <repo> . print the repositories that should be built before the [t]arget repository\n";
  die "\n";
}

my $warn = 0;
if( exists $opts{w} ) {
  ++$warn;
}

# variables

my (%repo_mods, %repo_deps, %mod_repos, %repo_tree, %repo_sorted);
my (@repo_list, @repo_sorted);
my ($dep, $branch_version);
my ($module, $xml, $data);
my @build_chain; # required repositories are stored here by build() subroutine
my %blocked = (); # helper for resolving build chain

# subs

sub collectModules {
  unless( /^pom.xml$/ ) {
    return;
  }
  my $dir = $File::Find::dir;

  my $this_repo = $dir;
  $this_repo =~ s#/.*##;

  $xml = new XML::Simple;
  $data = $xml->XMLin($_);
  $module = getModule($data, $this_repo);

  # collect repo module info
  ##########################
  if( ! exists $repo_mods{$this_repo} ) { 
    $repo_mods{$this_repo} = {};
  }
  $repo_mods{$this_repo}->{$module} = 1;

  # collect repo dependency info
  ##############################

  # add parent dependency
  my $parent_ref = $data->{'parent'};
  if ( keys %{$parent_ref} ) {
    recordDependency($parent_ref, $this_repo);
  } elsif ( $verbose ) {
    print STDERR "No parent for $module in $this_repo\n";
  }

  # add dependencies
  my $dep_arr_ref = $data->{'dependencies'}->{'dependency'};
  if( defined $dep_arr_ref ) {
    if( $dep_arr_ref =~ /^HASH/ ) {
      recordDependency($dep_arr_ref, $this_repo);
    } else {
      foreach my $dep (@{$dep_arr_ref}) {
        recordDependency($dep, $this_repo);
      }
    }
  }

  # add imported POMs from dependency management
  my $depmgmt_arr_ref = $data->{'dependencyManagement'}->{'dependencies'}->{'dependency'};
  if( defined $depmgmt_arr_ref ) {
    if( $depmgmt_arr_ref =~ /^HASH/ ) {
      recordDependency($depmgmt_arr_ref, $this_repo, 1);
    } else {
      foreach my $dep (@{$depmgmt_arr_ref}) {
        recordDependency($dep, $this_repo, 1);
      }
    }
  }
}

sub recordDependency {
  my $dep_id = "$_[0]->{'groupId'}:$_[0]->{'artifactId'}";
  my $this_repo = $_[1];
  my $isDependencyManagement = $_[2];
  if( $isDependencyManagement && ( ! defined $_[0]->{'scope'} || $_[0]->{'scope'} ne 'import' ) ) {
    return;
  }
  if( ! exists $repo_deps{$dep_id} ) {
    $repo_deps{$dep_id} = {};
  }
  $repo_deps{$dep_id}{$this_repo} = 1;
}

sub getModule() {
  my $xml_data = shift();
  my $repo = shift();

  my $groupId = $xml_data->{'groupId'};
  if( ! defined $groupId ) {
    $groupId = $xml_data->{'parent'}->{'groupId'};
  }
  my $module = "$groupId:$xml_data->{'artifactId'}";

  # check version 
  my $version = $xml_data->{'version'};
  if( ! defined $version ) {
    $version = $xml_data->{'parent'}->{'version'};
  }
  if( ! defined $branch_version ) { 
    $branch_version = $version;
  } elsif( $groupId !~ /^org.uberfire/ ) { 
    if( $branch_version ne $version ) { 
      my $msg = "Incorrect version ($version) for $module in $repo\n";
      if( $warn ) { 
        print STDERR $msg;
      } else {
        die "$msg";
      }
    }
  }

  return $module;
}

sub onlyLookAtPoms { 
  my @pom_files = grep { $_ =~ /pom.xml/ } @_;
  my @dirs = grep { -d $_ } @_;
  my @filesToProcess = ();

  foreach my $fileName ( "src", "target", "bin", "resources", "kie-eap-modules", "META-INF" ) { 
    @dirs = grep { ! ( $_ eq $fileName && -d $_ ) } @dirs;
  }

  @filesToProcess = (@pom_files, @dirs );

  return @filesToProcess;
}

sub build {
  my $target = shift();
  my $depth = shift();
  $blocked{$target} = 0;
  if( exists $repo_tree{$target} ) {
    # traverse the graph recursively
    foreach my $child ( sort keys %{$repo_tree{$target}} ) {
      if( scalar @{ ${repo_tree}{$target}{$child} } > 0 ) { # is direct dependency
        if( $verbose ) {
          printf( "%s%s -> %s\n", "- " x $depth, $target, $child );
        }
        if( exists ${blocked}{$child} && ${blocked}{$child} == 0 ) {
          # is already built
          next;
        }
        ++$blocked{$target};
        if( exists ${blocked}{$child} && ${blocked}{$child} > 0 ) {
          if( $verbose ) {
            print "CYCLE DETECTED!\n";
          }
          next;
        }
        build( $child, $depth + 1 );
        if( ${blocked}{$child} == 0 ) {
          --$blocked{$target};
        }
      }
    }
  }

  if ( $blocked{$target} == 0 ) {
    # either no deps or all direct deps built => append this target to build chain
    push( @build_chain, $target);
    if( $verbose ) { print "Building: $target\n"; }
  } else {
    print "Blocked repo: $target\n";
  }
}

sub filterTransitiveDependencies { 
  # Remove shortcut dependencies, 
  # i.e. remove each dependency A->C if path A->B->C exists.
  
  foreach my $dep_repo (keys %repo_tree) {
    foreach my $src_repo (keys %{$repo_tree{$dep_repo}} ) {
      foreach my $inter_repo (keys %{$repo_tree{$dep_repo}} ) {
        if( defined $repo_tree{$inter_repo}{$src_repo} ) {
          # do not delete the link entirely, it helps to detect 2+ step shortcuts
          $repo_tree{$dep_repo}{$src_repo} = [];
        }
      }
    }
  }
}

sub show { 
  if( $print_deps ) { 
    print shift();
  }
}

# main

my $script_home_dir = dirname(abs_path($0));
my $repo_file = dirname($script_home_dir) . "/repository-list.txt";
open(LIST, "<$repo_file" ) 
  || die "Unable to open $repo_file: $!\n";
while(<LIST>) { 
  chomp($_);
  push( @repo_list, $_ );
}

chdir "$script_home_dir/../../../";
my $root_dir = Cwd::getcwd();

for my $i (0 .. $#repo_list ) { 
  my $repo = $repo_list[$i];

  if( ! -d $repo ) { 
    die "Could not find directory for repository '$repo' at $root_dir!\n";
  } 

  find( {
    wanted => \&collectModules, 
    preprocess => \&onlyLookAtPoms
    }, $repo);
}

print STDERR "- Finished collecting module information.\n";

foreach my $repo (keys %repo_mods) {
  foreach $dep (keys %{$repo_mods{$repo}}) {
    if( exists $mod_repos{$dep} ) { 
      print STDERR "The $dep module exists in both the $mod_repos{$dep} AND $repo repositories!\n";
    } else { 
      $mod_repos{$dep} = $repo;
    }
  }
}

print STDERR "- Finished ordering module information.\n";

# repo_deps : dependency -> repository in which the dependency is used (dependent)
# mod_repos : module -> repository in which the module is located (source) 
foreach $dep ( keys %repo_deps ) {
  my $src_repo = $mod_repos{$dep};
  if( $src_repo ) { # otherwise it is a 3rd party artifact
    foreach my $dep_repo ( keys %{$repo_deps{$dep}} ) {
      if( $src_repo eq $dep_repo ) {
        # dependencies inside a repository are OK
        next;
      }
      if( ! exists $repo_tree{$dep_repo} ) { 
        $repo_tree{$dep_repo} = {};
      } 
      if( ! defined $repo_tree{$dep_repo}{$src_repo} ) { 
        $repo_tree{$dep_repo}{$src_repo} = [];
      } 
      $dep =~ s/^[^:]*://;
      push( @{ $repo_tree{$dep_repo}{$src_repo} }, $dep );
    }
  }
}

print STDERR "- Finished creating repository dependency tree.\n";

my %build_tree;

if( $filter_transitive ) { 
  filterTransitiveDependencies();
}

show( "\nDependent-on tree: \n" );
foreach my $repo (sort @repo_list) {
  show( "\n$repo (is dependent on): \n" );
  foreach my $leaf_repo (sort keys %{$repo_tree{$repo}} ) {
    my @deps = @{ $repo_tree{$repo}{$leaf_repo} };
    if( scalar @deps > 0 ) {
      my $deps_str = $verbose ? join( ',', @deps ) : scalar @deps;
      show( "- $leaf_repo ($deps_str)\n" );
    }
    if( ! exists $build_tree{$leaf_repo} ) { 
      $build_tree{$leaf_repo} = {};
    }
    ++$build_tree{$leaf_repo}{$repo};
  }
}

show( "\nDependencies tree: \n" );
foreach my $repo (sort keys %build_tree) {
  show( "\n$repo (is used by): \n" );
  foreach my $leaf_repo (sort keys %{$build_tree{$repo}} ) {
    show( "- $leaf_repo\n" );
  }
}

# Print the list of repositories required to be built before building target repository.
if ( defined $build_target ) {
  build( $build_target, 0 );

  if( $blocked{$build_target} > 0 ) {
    print STDERR "\nRepository '$build_target' cannot be built in a non-snapshot version due to circular dependencies!\n";
    if( ! $verbose ) { print STDERR "Re-run in verbose mode to see the cause.\n"; }
  }
  print STDERR "\nYou need to build following repositories before building '$build_target' (in order):\n";
  print join( ',', @build_chain ), "\n";
}

if( $create_dot_file ) { 
  if( ! $filter_transitive ) { 
    filterTransitiveDependencies();
  }
  # Transform the build graph into DOT language.
  my $dot = "digraph {\n";
  foreach my $repo (keys %repo_tree) {
    my $node_style = " [style=filled, fillcolor=lightskyblue]";
    $dot .= sprintf( "  \"%s\"%s;\n", $repo, ( grep ( /^$repo$/, @build_chain ) ) ? $node_style : "" );
    foreach my $leaf_repo (keys %{$repo_tree{$repo}}) {
      if ( scalar @{ $repo_tree{$repo}{$leaf_repo} } > 0) {
        $dot .= sprintf( "  \"%s\" -> \"%s\" [label=%s];\n",
                         $repo, $leaf_repo, scalar @{ $repo_tree{$repo}{$leaf_repo} } );
      }
    }
    $dot .= "\n";
  }
  $dot .= "}\n";
  
  # Write it to a file. The graph image can be produced simply by running
  # $ dot -O -Tpng dep-tree.dot
  my $filename = "dep-tree.dot";
  open(my $dotfile, ">", $filename) or die "Can't open dep-tree.dot: $!";
  print $dotfile $dot;
  close $dotfile or die "$dotfile: $!";
  printf "\nGraph written to '%s'.\n", abs_path( $filename );
  print "Run 'dot -O -Tpng $filename' to render PNG image.\n";
}

