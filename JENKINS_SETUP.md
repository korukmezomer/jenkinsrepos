# Jenkins CI/CD Kurulum Rehberi

Bu dokümantasyon, öğrenci otomasyon sistemini Jenkins ile CI/CD pipeline kurulumunu açıklar.

## Ön Gereksinimler

- Docker ve Docker Compose kurulu olmalı
- Git repository erişimi
- En az 4GB RAM (Jenkins için)

## 1. Docker Compose ile Jenkins ve PostgreSQL'i Başlatma

```bash
docker-compose up -d postgres jenkins
```

Jenkins ilk başlatıldığında admin şifresini görmek için:

```bash
docker exec otomasyonogrenci-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

## 2. Jenkins İlk Kurulum

1. Tarayıcıda `http://localhost:8081` adresine gidin
2. İlk admin şifresini girin (yukarıdaki komuttan aldığınız)
3. "Install suggested plugins" seçeneğini seçin
4. Admin kullanıcı oluşturun

## 3. Gerekli Jenkins Plugin'lerini Kurma

Jenkins Dashboard > Manage Jenkins > Manage Plugins > Available plugins

Şu plugin'leri kurun:
- **Pipeline** (genellikle zaten kurulu)
- **Docker Pipeline**
- **Git**
- **JUnit**
- **HTML Publisher** (code coverage raporları için)
- **Email Extension** (bildirimler için)

## 4. Docker Erişimi Ayarlama

Jenkins container'ının Docker'a erişebilmesi için:

```bash
# Jenkins container'ına girin
docker exec -it otomasyonogrenci-jenkins bash

# Docker socket izinlerini kontrol edin
ls -la /var/run/docker.sock
```

Eğer izin sorunu varsa:

```bash
# Host'ta çalıştırın
sudo chmod 666 /var/run/docker.sock
```

## 5. Jenkins Pipeline Job Oluşturma

1. Jenkins Dashboard > **New Item**
2. İsim: `otomasyonogrenci-pipeline`
3. **Pipeline** seçeneğini seçin
4. **OK** tıklayın

### Pipeline Yapılandırması

**Pipeline** sekmesinde:
- **Definition**: Pipeline script from SCM
- **SCM**: Git
- **Repository URL**: Git repository URL'nizi girin
- **Credentials**: Gerekirse Git credentials ekleyin
- **Branches to build**: `*/main` veya `*/master`
- **Script Path**: `Jenkinsfile`

**Save** tıklayın.

## 6. İlk Build'i Çalıştırma

1. Oluşturduğunuz pipeline job'a gidin
2. **Build Now** tıklayın
3. Build loglarını **Console Output**'tan takip edin

## 7. Webhook Kurulumu (Opsiyonel - Otomatik Trigger)

Git repository'nizde (GitHub, GitLab, Bitbucket) webhook ekleyin:

**GitHub için:**
1. Repository > Settings > Webhooks > Add webhook
2. Payload URL: `http://your-jenkins-url:8081/github-webhook/`
3. Content type: `application/json`
4. Events: `Just the push event`
5. Add webhook

**Jenkins'te:**
- Pipeline yapılandırmasında **Build Triggers** > **GitHub hook trigger for GITScm polling** seçin

## 8. Email Bildirimleri Ayarlama

1. Jenkins > Manage Jenkins > Configure System
2. **Extended E-mail Notification** bölümünü bulun
3. SMTP ayarlarınızı girin:
   - SMTP server
   - SMTP Port
   - User Name
   - Password
   - Use SSL: true (genellikle)
4. **Test configuration** ile test edin

## 9. Pipeline Aşamaları

Pipeline şu aşamalardan oluşur:

1. **Checkout**: Kod repository'den çekilir
2. **Build**: Gradle ile proje derlenir
3. **Unit Tests**: Birim testler çalıştırılır
4. **Integration Tests**: Entegrasyon testleri çalıştırılır
5. **Code Coverage**: Kod kapsama raporu oluşturulur
6. **Docker Build**: Docker image oluşturulur
7. **Docker Test**: Docker container test edilir
8. **Deploy**: Sadece main/master branch'te deploy edilir

## 10. Test Sonuçlarını Görüntüleme

- **Test Results**: Her build'in test sonuçları görüntülenebilir
- **Code Coverage Report**: HTML Publisher ile coverage raporları görüntülenir
- **Console Output**: Tüm build logları

## 11. Sorun Giderme

### Docker Permission Denied
```bash
sudo usermod -aG docker jenkins
# veya
sudo chmod 666 /var/run/docker.sock
```

### PostgreSQL Bağlantı Hatası
- Docker network'ün doğru kurulduğundan emin olun
- `docker network ls` ile network'leri kontrol edin
- `docker-compose.yml`'deki network ayarlarını kontrol edin

### Test Failures
- Test loglarını Console Output'tan kontrol edin
- Local'de testleri çalıştırın: `./gradlew test`
- Test database ayarlarını kontrol edin

### Build Timeout
- Jenkins > Manage Jenkins > Configure System
- Build timeout değerini artırın

## 12. Pipeline'ı Özelleştirme

`Jenkinsfile` dosyasını düzenleyerek:
- Farklı branch'ler için farklı aşamalar ekleyebilirsiniz
- Deployment stratejilerini değiştirebilirsiniz
- Notification ayarlarını özelleştirebilirsiniz

## 13. Production Deployment

Production için:
1. Ayrı bir deployment stage ekleyin
2. Production environment variables ekleyin
3. Blue-Green deployment stratejisi kullanabilirsiniz
4. Rollback mekanizması ekleyin

## Faydalı Komutlar

```bash
# Jenkins loglarını görüntüle
docker logs otomasyonogrenci-jenkins

# Jenkins container'ını yeniden başlat
docker restart otomasyonogrenci-jenkins

# Tüm servisleri durdur
docker-compose down

# Volume'ları da silerek durdur
docker-compose down -v
```

## Güvenlik Notları

1. Jenkins admin şifresini güçlü tutun
2. Git credentials'ları Jenkins Credentials Manager'da saklayın
3. Production secrets'ları environment variables olarak saklayın
4. Jenkins'i HTTPS ile çalıştırın (production için)
5. Firewall kurallarını doğru yapılandırın

