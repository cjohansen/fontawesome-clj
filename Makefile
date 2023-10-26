fontawesome.jar: src/fontawesome/*
	rm -f fontawesome.jar && clojure -A:dev -M:jar

deploy: fontawesome.jar
	mvn deploy:deploy-file -Dfile=fontawesome.jar -DrepositoryId=clojars -Durl=https://clojars.org/repo -DpomFile=pom.xml

.PHONY: deploy
