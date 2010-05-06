set EXEC_DIR=h:\oiaw

java -Xmx1024m -cp %EXEC_DIR%\lib\scala-library.jar;%EXEC_DIR%\lib\commons-cli-1.2.jar;%EXEC_DIR%\lib\scalatest-1.0.jar;%EXEC_DIR%\oiaw.jar eu.budabe.oiaw.OIAWrun %*
