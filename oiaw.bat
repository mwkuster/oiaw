set EXEC_DIR=generate
java -cp %EXEC_DIR%/lib/scala-library.jar;%EXEC_DIR%/lib/commons-cli-1.2.jar;%EXEC_DIR%/lib/scalatest-1.2.jar;%EXEC_DIR%/oiaw.jar eu.budabe.oiaw.OIAWrun %*
