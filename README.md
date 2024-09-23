# SongAPI
## Описание
Бекэнд-приложение предоставляет возможность пользователю загружать песни в S3-хранилище/некую локальную машину. 
Затем путем обращения к SpotifyAPI получать при последующей загрузке трека его мета-информацию.
Все это реализовано через обращение к эндпоинтам Tapir.
Перед использованием юзеру стоит зарегистрироваться на сервисе auth и использовать свои данные при последующих запросах в хедере Authorization(basic auth)
Юзеры и Мета(опционально) хранятся в PostgreSQL-базе данных
## Стек
- Java 17
- Scala  2.13.14
- sbt 1.10.1
- Tapir
- Akka-http
- Circe
- Doobie
- STTP-Client4
- Postgres
- AWS S3
- Docker

## Эндпоинты
#### api-host:8080
1. POST /upload 
2. GET /download
3. GET /dowloand-with-meta
#### auth-host:8081
1. GET /login
2. POST /register
## Установка и запуск
### Склонируйте репозиторий
`git clone git@github.com:incelcure/song-api.git`
### Установите переменные окружения(файл .env)
- CLIENT_ID - ID вашего spotifyAPI клиента
- CLIENT_SECRET - секретный ключ spotifyAPI клиента
- POSTGRES_DB_URL - ссылка на pg базу
- POSTGRES_USER и POSTGRES_PASSWORD - данные юзера pg
- S3_ACCESS_KEY, S3_SECRET_KEY, S3_HOST - данные вашего S3 хранилища (по дефолту можно запустить docker-compose файл с локалстеком)
- DOWNLOAD_PATH - директория для скачивания файлов из API

### Соберите проект
`sbt run`
#### Запустите ./auth/src/main/scala/App
#### Запустите ./src/main/scala/Main
## Enjoy!
### TODO
- [ ] Написать тесты
- [ ] Реализовать хранение меты в кэше (Redis)
- [ ] Сделать сваггер для эндопинтов
- [ ] Изменить структуру хранения songId и меты в БД (Хэш + Юзер)
