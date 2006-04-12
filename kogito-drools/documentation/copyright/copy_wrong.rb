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
          if File.directory? sub and not sub.include? ".svn" 
	  then do_dir(sub, copyright) end
        end        
      end
    }
end


#replace the guts of a file
def do_java(f, copyright)

  contents = IO.read(f)
  exp = Regexp.escape("/*") + ".*Copyright.*?" + Regexp.escape("*/") 
  existing = Regexp.new(exp, Regexp::MULTILINE)
  if not contents.include? copyright then
    if existing.match(contents) != nil 
      then 
        #clear out the old one
        puts "replacing in : " + f
        contents = contents.sub(existing, "")      
      else 
        puts "adding to : " + f
    end
    
    write_to f, put_in(contents, copyright)
    
  else 
    puts "ignoring as it is OK : " + f
  end    
  
end

def put_in(contents, copyright) 
  new_contents = ""
  line_num = 0
  contents.split(/\n/).each { |line| 
    if line_num == 0 then
      new_contents = line + "\n" + copyright 
    else 
      new_contents = new_contents + "\n" + line
    end
    line_num = line_num + 1
  }
  return new_contents
end

def write_to(target, guts) 
      target = File.new(target, "w")
      target.write guts
      target.close
end

do_dir("c:/temp/tryagain", IO.read ("c:/temp/copyright.txt")) 

