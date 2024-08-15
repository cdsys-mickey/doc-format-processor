chcp 65001
@if [%1]==[] goto usage
set MAVEN_OPTS=
mvnw clean package -P%1 -Dmaven.test.skip
:usage
@echo usage: %0 [profile]
@exit /B 1