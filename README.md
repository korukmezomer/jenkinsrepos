# Öğrenci Otomasyon Sistemi

Spring Boot tabanlı REST API ile geliştirilmiş öğrenci otomasyon sistemi. PostgreSQL veritabanı kullanılmaktadır.

## Özellikler

- **3 Rol Sistemi**: Admin, Öğretmen, Öğrenci
- **JWT Authentication**: Güvenli token tabanlı kimlik doğrulama
- **Ders Yönetimi**: Ders oluşturma, güncelleme, silme
- **Ders Kayıt**: Öğrencilerin derslere kayıt olması
- **Not Yönetimi**: Vize, final ve ortalama not hesaplama
- **RESTful API**: Tüm işlemler REST API üzerinden yapılır

## Teknolojiler

- Spring Boot 4.0.0
- PostgreSQL
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA
- Lombok

## Kurulum

### 1. PostgreSQL Veritabanı Kurulumu

PostgreSQL'in kurulu olduğundan emin olun ve aşağıdaki veritabanını oluşturun:

```sql
CREATE DATABASE otomasyonogrenci;
```

### 2. Veritabanı Bağlantı Ayarları

`src/main/resources/application.properties` dosyasındaki veritabanı bilgilerini kendi ayarlarınıza göre güncelleyin:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/otomasyonogrenci
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 3. Projeyi Çalıştırma

```bash
./gradlew build
./gradlew bootRun
```

Veya IDE'de `OtomasyonogrenciApplication` sınıfını çalıştırın.

## API Endpoints

### Authentication

- `POST /api/auth/signin` - Giriş yap
- `POST /api/auth/signup` - Kayıt ol

### Dersler (Courses)

- `GET /api/courses` - Tüm dersleri listele
- `GET /api/courses/{id}` - Ders detayı
- `GET /api/courses/code/{courseCode}` - Ders kodu ile ara
- `GET /api/courses/teacher/{teacherId}` - Öğretmenin dersleri
- `POST /api/courses?teacherId={id}` - Yeni ders oluştur (Öğretmen/Admin)
- `PUT /api/courses/{id}` - Ders güncelle (Öğretmen/Admin)
- `DELETE /api/courses/{id}` - Ders sil (Admin)

### Ders Kayıtları (Enrollments)

- `GET /api/enrollments` - Tüm kayıtları listele (Admin)
- `GET /api/enrollments/{id}` - Kayıt detayı
- `GET /api/enrollments/student/{studentId}` - Öğrencinin kayıtları
- `GET /api/enrollments/course/{courseId}` - Dersin kayıtları
- `POST /api/enrollments?studentId={id}&courseId={id}` - Derse kayıt ol (Öğrenci/Admin)
- `PUT /api/enrollments/{id}/drop` - Ders kaydını iptal et (Öğrenci/Admin)
- `DELETE /api/enrollments/{id}` - Kayıt sil (Admin)

### Notlar (Grades)

- `GET /api/grades` - Tüm notları listele (Admin)
- `GET /api/grades/{id}` - Not detayı
- `GET /api/grades/enrollment/{enrollmentId}` - Kayıt notu
- `GET /api/grades/student/{studentId}` - Öğrencinin notları
- `GET /api/grades/course/{courseId}` - Dersin notları
- `POST /api/grades?enrollmentId={id}&midtermGrade={not}&finalGrade={not}` - Not gir (Öğretmen/Admin)
- `DELETE /api/grades/{id}` - Not sil (Admin)

### Admin İşlemleri

- `GET /api/admin/users` - Tüm kullanıcıları listele
- `GET /api/admin/users/{id}` - Kullanıcı detayı
- `GET /api/admin/users/role/{role}` - Role göre kullanıcılar
- `GET /api/admin/students` - Tüm öğrenciler
- `GET /api/admin/teachers` - Tüm öğretmenler
- `PUT /api/admin/users/{id}` - Kullanıcı güncelle
- `DELETE /api/admin/users/{id}` - Kullanıcı sil

### Öğrenci İşlemleri

- `GET /api/student/enrollments` - Kendi kayıtlarım
- `GET /api/student/enrollments/{studentId}` - Öğrenci kayıtları
- `GET /api/student/grades/{studentId}` - Kendi notlarım
- `POST /api/student/enroll?studentId={id}&courseId={id}` - Derse kayıt ol
- `PUT /api/student/enrollments/{id}/drop` - Ders kaydını iptal et

### Öğretmen İşlemleri

- `GET /api/teacher/courses/{teacherId}` - Kendi derslerim
- `GET /api/teacher/courses/{courseId}/enrollments` - Dersin kayıtları
- `GET /api/teacher/courses/{courseId}/grades` - Dersin notları
- `POST /api/teacher/courses?teacherId={id}` - Yeni ders oluştur
- `PUT /api/teacher/courses/{id}` - Ders güncelle
- `POST /api/teacher/grades?enrollmentId={id}&midtermGrade={not}&finalGrade={not}` - Not gir

## Kullanım Örnekleri

### 1. Kullanıcı Kaydı

```bash
POST /api/auth/signup
Content-Type: application/json

{
  "username": "ogrenci1",
  "email": "ogrenci1@example.com",
  "password": "123456",
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "role": ["student"],
  "studentNumber": "2024001"
}
```

### 2. Giriş Yapma

```bash
POST /api/auth/signin
Content-Type: application/json

{
  "username": "ogrenci1",
  "password": "123456"
}
```

Response'da JWT token alınır. Bu token'ı Authorization header'ında kullanın:

```
Authorization: Bearer <token>
```

### 3. Ders Oluşturma (Öğretmen)

```bash
POST /api/courses?teacherId=1
Authorization: Bearer <token>
Content-Type: application/json

{
  "courseCode": "CS101",
  "courseName": "Bilgisayar Programlama",
  "description": "Temel programlama kavramları",
  "credit": 3,
  "quota": 50
}
```

### 4. Derse Kayıt Olma (Öğrenci)

```bash
POST /api/enrollments?studentId=1&courseId=1
Authorization: Bearer <token>
```

### 5. Not Girme (Öğretmen)

```bash
POST /api/grades?enrollmentId=1&midtermGrade=75&finalGrade=80
Authorization: Bearer <token>
```

## Roller

- **ROLE_ADMIN**: Tüm işlemlere erişim
- **ROLE_TEACHER**: Ders oluşturma, not girme
- **ROLE_STUDENT**: Ders kayıt, not görüntüleme

## Not Hesaplama

Notlar otomatik olarak hesaplanır:
- Ortalama = (Vize * 0.4) + (Final * 0.6)
- Harf notu ortalamaya göre belirlenir:
  - 90-100: AA
  - 85-89: BA
  - 80-84: BB
  - 75-79: CB
  - 70-74: CC
  - 65-69: DC
  - 60-64: DD
  - 50-59: FD
  - 0-49: FF

## Güvenlik

- Tüm API endpoint'leri (signin/signup hariç) JWT token gerektirir
- Roller bazlı yetkilendirme uygulanmıştır
- Şifreler BCrypt ile hash'lenmiştir

## Veritabanı Yapısı

Sistem başlatıldığında otomatik olarak şu tablolar oluşturulur:
- `users` - Kullanıcılar
- `roles` - Roller
- `user_roles` - Kullanıcı-Rol ilişkisi
- `courses` - Dersler
- `enrollments` - Ders kayıtları
- `grades` - Notlar

## Testler

Proje kapsamlı birim ve entegrasyon testleri içermektedir.

### Testleri Çalıştırma

```bash
# Tüm testleri çalıştır
./gradlew test

# Sadece birim testleri
./gradlew test --tests "*Test" --tests "*RepositoryTest" --tests "*ServiceTest"

# Sadece entegrasyon testleri
./gradlew test --tests "*IntegrationTest" --tests "*ControllerIntegrationTest"

# Code coverage raporu
./gradlew jacocoTestReport
```

Coverage raporu `build/reports/jacoco/test/html/index.html` dosyasında oluşturulur.

### Test Yapısı

- **Repository Tests**: Veritabanı işlemlerini test eder
- **Service Tests**: İş mantığını test eder (Mockito kullanarak)
- **Integration Tests**: API endpoint'lerini test eder (MockMvc kullanarak)

## CI/CD Pipeline (Jenkins)

Proje Jenkins ile otomatik test ve deployment için yapılandırılmıştır.

### Docker Compose ile Başlatma

```bash
# Tüm servisleri başlat (PostgreSQL + Jenkins + App)
docker-compose up -d

# Sadece PostgreSQL ve Jenkins
docker-compose up -d postgres jenkins
```

### Jenkins Erişimi

- Jenkins UI: http://localhost:8081
- İlk admin şifresi: `docker exec otomasyonogrenci-jenkins cat /var/jenkins_home/secrets/initialAdminPassword`

### Pipeline Aşamaları

1. **Checkout**: Kod repository'den çekilir
2. **Build**: Gradle ile proje derlenir
3. **Unit Tests**: Birim testler çalıştırılır
4. **Integration Tests**: Entegrasyon testleri çalıştırılır
5. **Code Coverage**: Kod kapsama raporu oluşturulur
6. **Docker Build**: Docker image oluşturulur
7. **Docker Test**: Docker container test edilir
8. **Deploy**: Sadece main/master branch'te deploy edilir

Detaylı kurulum için `JENKINS_SETUP.md` dosyasına bakın.

### Docker Image Oluşturma

```bash
# Docker image oluştur
docker build -t otomasyonogrenci-app .

# Container çalıştır
docker run -d \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/otomasyonogrenci \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SERVER_PORT=8082 \
  -p 8082:8082 \
  otomasyonogrenci-app
```

## Health Check

Actuator endpoint'leri ile uygulama sağlığı kontrol edilebilir:

```bash
curl http://localhost:8082/actuator/health
```

## Lisans

Bu proje eğitim amaçlı geliştirilmiştir.

