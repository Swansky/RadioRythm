# RadioRythm

A self host music bot!

## How to use

### docker / docker-compose

#### docker:
```dockerfile
docker run  -v conf:/usr/app/conf swansky/radiorythm
```
#### docker-compose:
```yml
version: '3.7'
services:
  radio-rythm:
    image: swansky/radiorythm:latest
    restart: always
    volumes:
      - ./radioRythm-data:/usr/app/conf
```


### settings
Settings.json will be generated at first run.
```json
{"token":"YOUR TOKEN HERE", 
  "tag":"S!",
  "language":"ENG"}
```

Settings name | description   |
--- |-------------------------------|
token | your token bot                 | 
tag | charactere to identify command exemple S!play | 
language | language of message check language table for all supported language | 


### Language Table

Language | code |
--- |------|
French | FR   | 
English | ENG  |



### artifact / jar 

- **Install JDK 17.**
- Download artifact

run:
````shell
java -jar RadioRythm.jar
````


