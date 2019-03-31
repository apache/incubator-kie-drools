#!/usr/bin/perl

#
# A "Split Package" is a package that is present in multiple modules (jars). 
#
# This script: 
# 1. identifies packages that are split over multiple jars. 
# 2. identifies Class names that are used in multiple modules
#
# Both things are a problem when using OSGi!
#

use strict;
use warnings;
use File::Find;

if( ! $ARGV[0] ) { 
  die "Please provide the directory that this should run in (as the first argument)!\n";
}

my (@searchDirs, %searchDirsMap);
foreach my $argDir (@ARGV) { 
  $searchDirsMap{$argDir} = 1;
}
@searchDirs = (keys %searchDirsMap);

my (%mainClassesMap, %mainSplitModules);
my (%testClassesMap, %testSplitModules); 

my %patternMapsMap = (
  "main" => [ \%mainClassesMap, \%mainSplitModules ],
  "test" => [ \%testClassesMap, \%testSplitModules ]
);

my %visited;
my %duplicate;

print "\n";

sub wanted {
  unless( /\.java$/ ) { 
    return;
  }

  my $file = $File::Find::name;

  my $module = 0;
  if( $file =~ m#([^\/]*)/src# ) { 
    $module = $1;
  } else { 
    die "No module name found for [$file]!\n";
  }

  my $inMainOrTest = 0;
  foreach my $dir ("main", "test") {
    my ($packagesRef, $splitPackagesRef);
    $packagesRef = $patternMapsMap{$dir}[0];
    $splitPackagesRef = $patternMapsMap{$dir}[1];

    if( $file =~ m#src/\Q$dir\E/java# ) { 
      my $pkg;
      if( $file =~ m#.*src/\Q$dir\E/java/(.*)/[^\/]*.java# ) { 
        $pkg = $1;
      } else { 
        die "[src/$dir/java] Unable to find package for java file: '$file'\n";
      }
      $pkg =~ s#\/#.#g;

      if( ! exists $packagesRef->{$pkg} ) { 
        $packagesRef->{$pkg} = $module;
      } else { 
        if( ! exists $splitPackagesRef->{$pkg} ) { 
          $splitPackagesRef->{$pkg} = [ $packagesRef->{$pkg} ];
        }
        my %splitPkgs = map { $_ => 1 } @{$splitPackagesRef->{$pkg}};
        if( ! exists $splitPkgs{$module} ) {
          push( @{$splitPackagesRef->{$pkg}}, $module );
        }
      }
      ++$inMainOrTest;
    } 
  }

  if( ! $inMainOrTest ) { 
    print "File is not located in src/main/java or src/test/java:\n  $file\n";
  }

};

sub preprocess { 
  my @javaFiles = grep { $_ =~ /.*\.java/ } @_;
  my @dirs = grep { -d $_ } @_;
  my @filesToProcess = ();

  foreach my $javaFile (@javaFiles) { 
    my $module;
    if( $File::Find::name =~ m#([^\/]*)/src# ) { 
     $module = $1;
    } else { 
      next;
    }

    my $pkg = $File::Find::name;
    $pkg =~ s#.*src/(main|test)/java/##;
    $pkg =~ s#/#.#g;

    my $classname = "$pkg.$javaFile";
    $classname =~ s/.java$//;

    if( ! exists $visited{$classname} ) { 
      $visited{$classname} = $module;
    } else { 
      if( ! exists $duplicate{$classname} ) { 
        $duplicate{$classname} = [ $visited{$classname} ];
      }
      push( @{$duplicate{$classname}}, $module );
    }
  }

  if( @javaFiles ) { 
    @javaFiles = ( $javaFiles[0] );
    push( @filesToProcess, $javaFiles[0] );
  }
  @filesToProcess = (@filesToProcess, @dirs );

  foreach my $fileName ( "target", "bin", "resources" ) { 
    @filesToProcess = grep { ! ( $_ eq $fileName && -d $_ ) } @filesToProcess;
  }

  return @filesToProcess;
}

find( { 
  wanted => \&wanted,
  preprocess => \&preprocess
  }, @searchDirs);


my ($pkg, $module);
my %lists = (
  "main" => \%mainSplitModules, 
  "test" => \%testSplitModules 
);

foreach my $type (sort keys %lists) { 

  print "\nPackages found in src/$type/java: \n\n";

  foreach $pkg (sort keys %{$lists{$type}}) { 
    print "$pkg: \n";
    foreach $module (@{$lists{$type}->{$pkg}}) { 
      print "  $module\n";
    }
  }
}

print "\n\nDuplicate class names: \n\n";

foreach my $class (sort keys %duplicate) { 
  print "$class: \n";
  foreach $module (@{$duplicate{$class}}) { 
    print "  $module\n";
  }
}

print "\n";
