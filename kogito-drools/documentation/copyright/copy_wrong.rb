#This utility aims to replace the copyright notices in source files with a given one.
#it will add one if it doesn't already exist.

#directory recursive walker...
def do_dir(start, copyright) 
    Dir.foreach(start) { |d| 
      if d != "." && d != ".." then
        sub = start + "/" + d        
        if not File.directory? sub and sub.include? ".java" then
          do_java(sub, copyright)
        else 
          if File.directory? sub then do_dir(sub, copyright) end
        end        
      end
    }
end


#replace the guys of a file
def do_java(f, copyright)

  contents = IO.read(f)
  ex_s = Regexp.escape("/*") + ".*Copyright.*" + Regexp.escape("*/") #regex not quite right, not matching end correctly
  puts "regex: " + ex_s
  ex = Regexp.new(ex_s, Regexp::MULTILINE)
  
  if ex.match(contents) != nil 
    then 
      #clear out the old one
      puts "replacing in : " + f
      new_guts = contents.sub(ex, "")      
    else 
      puts "adding to : " + f
      new_guts = contents
  end
  puts "NEW: " + new_guts
  
  
  lines = new_guts.split(/\n/)
  #now insert, after the 2nd line, the new (c) and we are done.
  
end

def write_to(target, guts) 
      target = File.new(f, "w")
      target.write guts
      target.close
end

do_dir("c:/temp/src/aaa", IO.read("c:/temp/copy.txt"))

