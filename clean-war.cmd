chcp 65001
@if [%1]==[] goto usage
set MAVEN_OPTS=
mvnw clean compile war:war -P%1
:usage
@echo usage: %0 [profile]
@exit /B 1