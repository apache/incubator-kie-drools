
if [ -a src/main/resources/META-INF/persistence.xml  ]; then
  echo "Setting up to test with db..";
  ( cd src/test/filtered-resources/META-INF/ && mv persistence.xml.real persistence.xml );
  ( cd src/main/resources/META-INF/ && mv persistence.xml persistence.xml.ddl-check );
elif [ -a src/test/filtered-resources/META-INF/persistence.xml ]; then
  echo "Setting up to check ddl..";
  ( cd src/main/resources/META-INF/ && mv persistence.xml.ddl-check persistence.xml )
  ( cd src/test/filtered-resources/META-INF/ && mv persistence.xml persistence.xml.real )
else 
  echo $1 '???';
fi

