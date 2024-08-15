cls
@if [%1]==[] goto usage
mvnw spring-boot:run -P %1 %2 %3
:usage
@echo usage: %0 [profile]
@exit /B 1