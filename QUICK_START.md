# Hızlı Başlangıç - Docker PostgreSQL ile

## 🚀 En Hızlı Yöntem (Önerilen)

### 1. PostgreSQL Container'ını Başlat
```bash
docker-compose up -d postgres
```

### 2. Container'ın Hazır Olduğunu Kontrol Et
```bash
docker ps
# veya
docker-compose ps
```

PostgreSQL container'ı `otomasyonogrenci-postgres` adıyla çalışıyor olmalı.

### 3. Uygulamayı Çalıştır
```bash
./gradlew bootRun
```

**Hepsi bu kadar!** 🎉

Uygulama şu adresten erişilebilir: http://localhost:8082

---

## 📋 Alternatif: Tüm Servisleri Docker'da Çalıştırma

Eğer uygulamayı da Docker'da çalıştırmak isterseniz:

```bash
# Tüm servisleri başlat (PostgreSQL + App + Jenkins)
docker-compose up -d

# Sadece PostgreSQL + App (Jenkins olmadan)
docker-compose up -d postgres app
```

Uygulama: http://localhost:8082  
Jenkins: http://localhost:8081

---

## ✅ Bağlantıyı Test Etme

### 1. Health Check
```bash
curl http://localhost:8082/actuator/health
```

### 2. PostgreSQL'e Direkt Bağlanma
```bash
docker exec -it otomasyonogrenci-postgres psql -U postgres -d otomasyonogrenci
```

### 3. Veritabanı Tablolarını Kontrol Etme
```sql
\dt
```

---

## 🔧 Sorun Giderme

### PostgreSQL container'ı çalışmıyor
```bash
# Logları kontrol et
docker logs otomasyonogrenci-postgres

# Container'ı yeniden başlat
docker-compose restart postgres
```

### Port 5432 zaten kullanılıyor
Eğer local'de PostgreSQL kuruluysa, docker-compose.yml'de port'u değiştirin:
```yaml
ports:
  - "5433:5432"  # 5433 kullan
```

Sonra application.properties'i güncelleyin:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/otomasyonogrenci
```

### Veritabanı bağlantı hatası
1. Container'ın çalıştığını kontrol edin: `docker ps`
2. Network'ü kontrol edin: `docker network ls`
3. Logları kontrol edin: `docker-compose logs postgres`

---

## 🛑 Servisleri Durdurma

```bash
# Tüm servisleri durdur
docker-compose down

# Servisleri durdur ve volume'ları sil (veriler silinir!)
docker-compose down -v
```

---

## 📝 Notlar

- İlk çalıştırmada PostgreSQL otomatik olarak `otomasyonogrenci` veritabanını oluşturur
- Spring Boot uygulaması başladığında tablolar otomatik oluşturulur (`spring.jpa.hibernate.ddl-auto=update`)
- Veriler `postgres_data` volume'unda saklanır
- Container'ı yeniden başlatsanız bile veriler korunur

