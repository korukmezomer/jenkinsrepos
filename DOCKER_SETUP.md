# Docker ile PostgreSQL Kullanım Rehberi

Bu dokümantasyon, projeyi Docker'daki PostgreSQL ile çalıştırmanın iki farklı yöntemini açıklar.

## Yöntem 1: Sadece PostgreSQL'i Docker'da Çalıştırma (Uygulama Local'de)

Bu yöntemde sadece PostgreSQL container'ı çalışır, Spring Boot uygulaması local'de çalışır.

### Adımlar:

1. **PostgreSQL container'ını başlat:**
```bash
docker-compose up -d postgres
```

2. **Container'ın çalıştığını kontrol et:**
```bash
docker ps
# veya
docker-compose ps
```

3. **Veritabanı bağlantısını test et:**
```bash
docker exec -it otomasyonogrenci-postgres psql -U postgres -d otomasyonogrenci
```

4. **Uygulamayı local'de çalıştır:**
```bash
./gradlew bootRun
```

Uygulama `localhost:5432` üzerinden PostgreSQL'e bağlanacak (application.properties'te zaten ayarlı).

### Avantajları:
- Hızlı geliştirme (hot reload çalışır)
- Debug yapmak kolay
- IDE'den direkt çalıştırabilirsiniz

---

## Yöntem 2: Tüm Servisleri Docker'da Çalıştırma (Docker Compose)

Bu yöntemde PostgreSQL, Jenkins ve uygulama hepsi Docker container'larında çalışır.

### Adımlar:

1. **Tüm servisleri başlat:**
```bash
docker-compose up -d
```

2. **Servislerin durumunu kontrol et:**
```bash
docker-compose ps
```

3. **Logları izle:**
```bash
# Tüm servislerin logları
docker-compose logs -f

# Sadece uygulama logları
docker-compose logs -f app

# Sadece PostgreSQL logları
docker-compose logs -f postgres
```

4. **Uygulamaya eriş:**
- API: http://localhost:8082
- Jenkins: http://localhost:8081
- PostgreSQL: localhost:5432

### Avantajları:
- Production'a yakın ortam
- Tüm servisler izole
- Kolay deployment

---

## Yöntem 3: Sadece PostgreSQL ve Uygulamayı Docker'da Çalıştırma

Jenkins olmadan sadece PostgreSQL ve uygulamayı çalıştırmak için:

```bash
docker-compose up -d postgres app
```

---

## Environment Variables ile Yapılandırma

Docker Compose'da environment variable'lar kullanılıyor. `application.properties`'teki değerler override edilir:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/otomasyonogrenci
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: postgres
```

**Not:** Docker container içinde `postgres` hostname'i kullanılır (Docker network sayesinde).

---

## Sorun Giderme

### PostgreSQL'e bağlanamıyorum

1. **Container'ın çalıştığını kontrol edin:**
```bash
docker ps | grep postgres
```

2. **Port'un açık olduğunu kontrol edin:**
```bash
netstat -an | grep 5432
# veya
lsof -i :5432
```

3. **Container loglarını kontrol edin:**
```bash
docker logs otomasyonogrenci-postgres
```

### Veritabanı bulunamıyor

PostgreSQL container'ı ilk başlatıldığında otomatik olarak `otomasyonogrenci` veritabanını oluşturur. Eğer oluşmamışsa:

```bash
docker exec -it otomasyonogrenci-postgres psql -U postgres
```

Sonra:
```sql
CREATE DATABASE otomasyonogrenci;
```

### Network sorunları

Container'lar aynı network'te olmalı. Kontrol edin:

```bash
docker network inspect otomasyonogrenci_otomasyon-network
```

### Verileri sıfırlama

Tüm verileri silmek için:

```bash
docker-compose down -v
```

Bu komut container'ları durdurur ve volume'ları siler.

---

## Veritabanı Yönetimi

### pgAdmin ile bağlanma

`docker-compose.yml`'e pgAdmin ekleyebilirsiniz:

```yaml
pgadmin:
  image: dpage/pgadmin4
  container_name: otomasyonogrenci-pgadmin
  environment:
    PGADMIN_DEFAULT_EMAIL: admin@admin.com
    PGADMIN_DEFAULT_PASSWORD: admin
  ports:
    - "5050:80"
  networks:
    - otomasyon-network
  depends_on:
    - postgres
```

Sonra http://localhost:5050 adresinden erişebilirsiniz.

### Veritabanı yedekleme

```bash
docker exec otomasyonogrenci-postgres pg_dump -U postgres otomasyonogrenci > backup.sql
```

### Veritabanı geri yükleme

```bash
docker exec -i otomasyonogrenci-postgres psql -U postgres otomasyonogrenci < backup.sql
```

---

## Production için Öneriler

1. **Güçlü şifreler kullanın:**
```yaml
environment:
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}  # .env dosyasından oku
```

2. **Volume'ları yedekleyin:**
```bash
docker run --rm -v otomasyonogrenci_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz /data
```

3. **Health check'leri aktif tutun**
4. **Resource limitleri koyun**
5. **SSL/TLS kullanın**

