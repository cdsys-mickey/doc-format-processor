cls
@if [%1]==[] goto usage
mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005" -P%1
:usage
@echo usage: %0 [profile]
@exit /B 1