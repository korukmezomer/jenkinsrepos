# GitHub Webhook ile Jenkins Otomatik Test Kurulumu

Bu rehber, GitHub'a her commit atıldığında Jenkins'in otomatik olarak testleri çalıştırmasını sağlar.

## 📋 Adım 1: Jenkins'i Başlatma

```bash
docker-compose up -d jenkins postgres
```

Jenkins admin şifresini alın:
```bash
docker exec otomasyonogrenci-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Jenkins'e giriş yapın: http://localhost:8081

---

## 🔌 Adım 2: Gerekli Jenkins Plugin'lerini Kurma

1. **Jenkins Dashboard** > **Manage Jenkins** > **Manage Plugins**
2. **Available** sekmesine gidin
3. Şu plugin'leri arayıp kurun:
   - ✅ **GitHub plugin** (GitHub entegrasyonu için)
   - ✅ **GitHub Branch Source Plugin** (branch yönetimi için)
   - ✅ **Pipeline** (zaten kurulu olabilir)
   - ✅ **Docker Pipeline**
   - ✅ **JUnit** (test sonuçları için)
   - ✅ **HTML Publisher** (code coverage için)

4. **Install without restart** tıklayın
5. Kurulum bitince Jenkins'i yeniden başlatın (gerekirse)

---

## 🔐 Adım 3: GitHub Personal Access Token Oluşturma

1. GitHub'a giriş yapın
2. **Settings** > **Developer settings** > **Personal access tokens** > **Tokens (classic)**
3. **Generate new token (classic)** tıklayın
4. Token ayarları:
   - **Note**: `Jenkins CI/CD`
   - **Expiration**: İstediğiniz süre (örn: 90 days)
   - **Scopes**: Şunları seçin:
     - ✅ `repo` (Full control of private repositories)
     - ✅ `admin:repo_hook` (Full control of repository hooks)
     - ✅ `read:org` (Read org and team membership)

5. **Generate token** tıklayın
6. **Token'ı kopyalayın** (bir daha gösterilmeyecek!)

---

## 🔑 Adım 4: Jenkins'te GitHub Credentials Ekleme

### Detaylı Adımlar:

1. **Jenkins Dashboard'a gidin** (http://localhost:8081)

2. **Manage Jenkins** tıklayın (sol menüde)

3. **Credentials** tıklayın

4. **System** tıklayın (veya **Global (unrestricted)**)

5. **Add Credentials** tıklayın (veya **Global credentials** > **Add Credentials**)

6. **Credentials formunu doldurun:**
   
   - **Kind**: Dropdown'dan **`Secret text`** seçin
   
   - **Secret**: GitHub'dan kopyaladığınız Personal Access Token'ı yapıştırın
     > Örnek: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`
   
   - **ID**: `github-token` yazın (veya istediğiniz bir ID)
     > Bu ID'yi pipeline yapılandırmasında kullanacaksınız
   
   - **Description**: `GitHub Personal Access Token` yazın
     > Açıklama (opsiyonel ama önerilir)

7. **OK** tıklayın

8. ✅ Credentials listesinde göründüğünü kontrol edin

### Alternatif: Username/Password ile (Token kullanarak)

Eğer Secret text çalışmazsa:

1. **Kind**: `Username with password` seçin
2. **Username**: GitHub kullanıcı adınızı girin
3. **Password**: GitHub Personal Access Token'ınızı girin (şifre değil, token!)
4. **ID**: `github-credentials` yazın
5. **OK** tıklayın

### Credentials Kontrolü:

Credentials'ı ekledikten sonra:
- Pipeline yapılandırmasında **Credentials** dropdown'ında görünmeli
- Eğer görünmüyorsa, sayfayı yenileyin (F5)
- Hala görünmüyorsa, credentials'ı tekrar kontrol edin

---

## 📦 Adım 5: Jenkins Pipeline Job Oluşturma

1. **Jenkins Dashboard** > **New Item**
2. **Item name**: `otomasyonogrenci-pipeline`
3. **Pipeline** seçeneğini seçin
4. **OK** tıklayın

### Pipeline Yapılandırması:

#### General Sekmesi:
- ✅ **GitHub project** işaretleyin
- **Project url**: `https://github.com/KULLANICI_ADI/REPO_ADI` (GitHub repo URL'niz)

#### Build Triggers Sekmesi:
- ✅ **GitHub hook trigger for GITScm polling** işaretleyin
- ✅ **Poll SCM** işaretleyin (opsiyonel, yedek olarak)
  - **Schedule**: `H/5 * * * *` (her 5 dakikada bir kontrol eder)

#### Pipeline Sekmesi:
- **Definition**: `Pipeline script from SCM`
- **SCM**: `Git`
- **Repository URL**: `https://github.com/KULLANICI_ADI/REPO_ADI.git`
  > Örnek: `https://github.com/omerkorukmez/otomasyonogrenci.git`
  
- **Credentials**: 
  - Dropdown'dan **`github-token`** (veya oluşturduğunuz ID) seçin
  - ⚠️ **"none" görünüyorsa**: Yukarıdaki Adım 4'ü tekrar kontrol edin
  - Credentials ekledikten sonra sayfayı yenileyin (F5)
  
- **Branches to build**: 
  - Branch Specifier: `*/main` veya `*/master` (repo'nuzdaki ana branch)
  > Eğer branch adınız farklıysa (örn: `master`), ona göre ayarlayın
  
- **Script Path**: `Jenkinsfile` (repo root'unda olmalı)
  > Jenkinsfile dosyası repository'nizin root dizininde olmalı

#### Advanced (opsiyonel):
- **Checkout to a sub-directory**: Boş bırakın
- **Additional Behaviours**: 
  - **Clean before checkout** ekleyebilirsiniz

5. **Save** tıklayın

---

## 🪝 Adım 6: GitHub Webhook Ekleme

### Yöntem 1: GitHub Web UI (Önerilen)

1. GitHub repository'nize gidin
2. **Settings** > **Webhooks** > **Add webhook**
3. Webhook ayarları:
   - **Payload URL**: 
     ```
     http://YOUR_PUBLIC_IP:8081/github-webhook/
     ```
     > **Not**: Eğer local'de çalışıyorsanız, ngrok veya benzeri bir tool kullanmanız gerekir (aşağıda açıklanacak)
   
   - **Content type**: `application/json`
   - **Secret**: Boş bırakabilirsiniz (veya güvenlik için bir secret ekleyin)
   - **Which events**: 
     - ✅ **Just the push event** (sadece push için)
     - VEYA
     - ✅ **Let me select individual events**:
       - ✅ `Pushes`
       - ✅ `Pull requests` (opsiyonel)
   
   - ✅ **Active** işaretli olmalı

4. **Add webhook** tıklayın
5. Webhook'un test edildiğini kontrol edin (yeşil tik görünmeli)

### Yöntem 2: Local Development için ngrok (Önerilen)

Eğer Jenkins local'de çalışıyorsa, GitHub'dan erişilebilir olması için ngrok kullanın:

1. **ngrok kurulumu:**
   ```bash
   # macOS
   brew install ngrok
   
   # veya https://ngrok.com/download adresinden indirin
   ```

2. **ngrok'u başlatın:**
   ```bash
   ngrok http 8081
   ```

3. **Forwarding URL'i kopyalayın** (örn: `https://abc123.ngrok.io`)

4. **GitHub webhook'ta bu URL'i kullanın:**
   ```
   https://abc123.ngrok.io/github-webhook/
   ```

5. **Not**: ngrok ücretsiz planında URL her restart'ta değişir. Kalıcı URL için ücretli plan gerekir.

---

## ✅ Adım 7: Test Etme

1. **Manuel Test:**
   - Jenkins'te pipeline job'unuza gidin
   - **Build Now** tıklayın
   - Console Output'tan build'in başarılı olduğunu kontrol edin

2. **Webhook Testi:**
   - GitHub repository'nize gidin
   - Herhangi bir dosyada küçük bir değişiklik yapın (örn: README'ye bir satır ekleyin)
   - Commit ve push yapın:
     ```bash
     git add .
     git commit -m "test: Jenkins webhook testi"
     git push origin main
     ```
   
   - Jenkins Dashboard'a gidin
   - Pipeline job'unuzun otomatik olarak başladığını görmelisiniz! 🎉

---

## 🔍 Adım 8: Webhook Durumunu Kontrol Etme

1. GitHub > Repository > **Settings** > **Webhooks**
2. Webhook'unuzun yanında:
   - ✅ **Yeşil tik**: Başarılı
   - ❌ **Kırmızı X**: Hata var
   - ⚠️ **Sarı uyarı**: Son delivery'de sorun var

3. **Recent Deliveries** sekmesinden webhook çağrılarını görebilirsiniz
4. Her delivery'ye tıklayarak request/response detaylarını görebilirsiniz

---

## 🐛 Sorun Giderme

### Webhook Çalışmıyor

1. **Jenkins loglarını kontrol edin:**
   ```bash
   docker logs otomasyonogrenci-jenkins -f
   ```

2. **GitHub webhook delivery'lerini kontrol edin:**
   - GitHub > Settings > Webhooks > Recent Deliveries
   - Hata mesajlarını inceleyin

3. **Jenkins'te GitHub plugin'in kurulu olduğundan emin olun**

4. **Build Triggers'da "GitHub hook trigger" seçili mi kontrol edin**

### ngrok URL Değişti

1. Yeni ngrok URL'ini alın
2. GitHub webhook'u güncelleyin

### Credentials Hatası / "None" Görünüyor

**Sorun**: Pipeline yapılandırmasında Credentials dropdown'ında "none" görünüyor.

**Çözüm Adımları:**

1. **Credentials'ın eklendiğini kontrol edin:**
   - Jenkins > Manage Jenkins > Credentials
   - System > Global credentials (unrestricted)
   - `github-token` (veya oluşturduğunuz ID) listede görünmeli

2. **Eğer yoksa, tekrar ekleyin:**
   - Add Credentials tıklayın
   - Kind: `Secret text`
   - Secret: GitHub token'ınızı yapıştırın
   - ID: `github-token`
   - OK tıklayın

3. **Pipeline sayfasını yenileyin:**
   - Pipeline job'unuza gidin
   - Configure tıklayın
   - Pipeline sekmesine gidin
   - Sayfayı yenileyin (F5)
   - Credentials dropdown'ında artık token'ınız görünmeli

4. **Alternatif: Username/Password kullanın:**
   - Kind: `Username with password`
   - Username: GitHub kullanıcı adınız
   - Password: GitHub token'ınız (şifre değil!)
   - ID: `github-credentials`

5. **Token'ın geçerli olduğunu kontrol edin:**
   - GitHub > Settings > Developer settings > Personal access tokens
   - Token'ınızın aktif olduğundan emin olun
   - Gerekirse yeni token oluşturun

6. **Repository URL'ini kontrol edin:**
   - HTTPS kullanıyorsanız credentials gerekir
   - Public repo için credentials opsiyonel olabilir
   - Private repo için mutlaka gerekir

### Pipeline Başlamıyor

1. **Poll SCM** aktif mi kontrol edin (yedek olarak)
2. Manuel olarak **Build Now** ile test edin
3. Console Output'tan hata mesajlarını kontrol edin

---

## 📊 Test Sonuçlarını Görüntüleme

Jenkins'te her build'den sonra:

1. **Test Results**: Build sayfasında test sonuçları görüntülenir
2. **Code Coverage**: HTML Publisher ile coverage raporları
3. **Console Output**: Tüm build logları
4. **Build History**: Tüm build geçmişi

---

## 🎯 İleri Seviye Yapılandırma

### Sadece Belirli Branch'lerde Çalıştırma

Jenkinsfile'da:
```groovy
when {
    anyOf {
        branch 'main'
        branch 'develop'
    }
}
```

### Pull Request'lerde Test

GitHub webhook'ta:
- **Pull requests** event'ini seçin
- Jenkinsfile'da PR için özel stage ekleyin

### Email Bildirimleri

1. **Email Extension Plugin** kurun
2. Pipeline'da email gönderme ekleyin (Jenkinsfile'da zaten var)

---

## 📝 Özet Checklist

- [ ] Jenkins başlatıldı ve erişilebilir
- [ ] GitHub plugin kuruldu
- [ ] GitHub Personal Access Token oluşturuldu
- [ ] Jenkins'te credentials eklendi
- [ ] Pipeline job oluşturuldu ve yapılandırıldı
- [ ] GitHub webhook eklendi
- [ ] ngrok kuruldu (local için)
- [ ] Test commit yapıldı ve pipeline otomatik başladı

---

## 🚀 Artık Hazırsınız!

Her GitHub commit'inizde Jenkins otomatik olarak:
1. ✅ Kodu çekecek
2. ✅ Build yapacak
3. ✅ Unit testleri çalıştıracak
4. ✅ Integration testleri çalıştıracak
5. ✅ Code coverage raporu oluşturacak
6. ✅ Docker image oluşturacak
7. ✅ Test edecek
8. ✅ (Main branch'te) Deploy edecek

Başarılar! 🎉

