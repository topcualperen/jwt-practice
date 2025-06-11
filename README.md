# Spring Boot JWT Authentication Demo

Bu proje, Spring Boot kullanarak JWT (JSON Web Token) tabanlı kimlik doğrulama ve yetkilendirme sisteminin nasıl uygulanacağını gösteren kapsamlı bir demo uygulamasıdır.

## 📋 Proje Hakkında

Bu proje, modern web uygulamalarında yaygın olarak kullanılan JWT tabanlı kimlik doğrulama sistemini Spring Boot framework'ü ile nasıl implemente edileceğini göstermektedir. Kullanıcılar sisteme kaydolabilir, giriş yapabilir ve güvenli endpoint'lere erişebilirler.

### 🛠 Kullanılan Teknolojiler

- **Spring Boot 3.5.0**: Ana framework
- **Spring Security**: Güvenlik ve kimlik doğrulama
- **Spring Data JPA**: Veritabanı işlemleri
- **H2 Database**: In-memory veritabanı
- **JWT (JSON Web Token)**: Token tabanlı kimlik doğrulama
- **Lombok**: Kod tekrarını azaltmak için
- **Java 17**: Programlama dili

## 🏗 Proje Yapısı

Proje aşağıdaki ana bileşenlerden oluşmaktadır:

### 📁 Paket Yapısı

```
com.example.jwt_demo/
├── config/           # Konfigürasyon sınıfları
├── controller/       # REST endpoint'leri
├── dto/             # Veri transfer objeleri
├── handler/         # Global exception handler
├── model/           # Veritabanı entity'leri
├── repository/      # JPA repository'leri
├── runner/          # Uygulama başlangıç dataları
├── security/        # JWT güvenlik bileşenleri
└── service/         # İş mantığı servisleri
```

### 🔑 Önemli Bileşenler ve Görevleri

#### 1. Security Paketi

- **JwtAuthFilter**: 
  - Her HTTP isteğini kontrol eden filter
  - Token'ın varlığını ve geçerliliğini kontrol eder
  - Geçerli token'lardan kullanıcı bilgilerini çıkarır
  - Spring Security context'ine kullanıcı bilgilerini yerleştirir

- **JwtUtils**:
  - Token oluşturma
  - Token'dan kullanıcı bilgilerini çıkarma
  - Token doğrulama
  
#### 2. Controller Paketi

- **AuthController**: 
  - `/api/auth/register`: Yeni kullanıcı kaydı
  - `/api/auth/login`: Kullanıcı girişi ve token üretimi
  
- **TestController**:
  - Güvenli endpoint örnekleri
  - Token doğrulaması gerektiren istekler

#### 3. Model ve DTO'lar

- **User**: 
  - Kullanıcı entity'si
  - Kullanıcı bilgilerini veritabanında saklar

- **AuthRequest/AuthResponse**: 
  - Giriş isteği ve yanıtı için veri transfer objeleri
  - Kullanıcı bilgileri ve JWT token'ı taşır

### 🔒 Güvenlik Akışı

1. **Kayıt İşlemi**:
   - Kullanıcı kayıt endpoint'ine username, password ve role bilgilerini içeren POST isteği yapar
   - Şifre BCrypt ile hash'lenir
   - Kullanıcı bilgileri veritabanına kaydedilir
   
2. **Giriş İşlemi**:
   - Kullanıcı giriş endpoint'ine credentials gönderir
   - AuthenticationManager kimlik doğrulaması yapar
   - Başarılı ise JWT token üretilir ve kullanıcıya döndürülür

3. **Güvenli İstekler**:
   - İstek header'ında "Bearer [token]" formatında token gönderilir
   - JwtAuthFilter token'ı doğrular
   - Geçerli token varsa istek işlenir

### 📝 Önemli Anotasyonlar

- `@Component`: Spring bean'i olarak işaretler
- `@RestController`: REST endpoint'lerini tanımlar
- `@RequestMapping`: API yolunu belirler
- `@Autowired`: Dependency injection için
- `@PostMapping`: POST isteklerini işler
- `@RequestBody`: HTTP body'sinden veri alır
- `@Service`: Servis katmanı bileşenlerini işaretler
- `@Repository`: Veritabanı işlemlerini yapan sınıfları işaretler

## 🚀 Başlangıç

### Gereksinimler

- Java 17 veya üzeri
- Maven

### Kurulum

1. Projeyi klonlayın:
```bash
git clone [repo-url]
```

2. Proje dizinine gidin:
```bash
cd jwt-demo
```

3. Maven ile derleyin:
```bash
mvn clean install
```

4. Uygulamayı çalıştırın:
```bash
mvn spring-boot:run
```

### 🔥 API Kullanımı

#### 1. Kullanıcı Kaydı
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123",
    "role": "ROLE_USER"  # ROLE_USER veya ROLE_ADMIN olmalı
}
```

#### 2. Giriş ve Token Alma
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123"
}
```

#### 3. Güvenli Endpoint'e İstek
```http
# Tüm authenticated kullanıcılar erişebilir
GET /api/test/hello
Authorization: Bearer [your-jwt-token]

# Sadece ROLE_ADMIN rolüne sahip kullanıcılar erişebilir
GET /api/test/admin
Authorization: Bearer [your-jwt-token]

Yanıtlar:
- /api/test/hello: "JWT ile korunan hello endpointi"
- /api/test/admin: "JWT ile korunan ve sadece ADMIN tarafından erişilen endpoint"
```

### 📬 API Yanıt Formatları

#### Başarılı Kayıt
```json
{
    "message": "Kullanıcı kaydedildi"
}
```

Önemli: Kayıt işleminde role alanı zorunludur ve aşağıdaki değerlerden birini almalıdır:
- `ROLE_USER`: Normal kullanıcı rolü
- `ROLE_ADMIN`: Yönetici rolü

#### Başarılı Giriş
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Hata Yanıtları
```json
// Geçersiz kimlik bilgileri (401)
{
    "message": "Geçersiz kimlik bilgileri"
}

// JWT süre aşımı veya geçersiz token (403)
{
    "message": "JWT süresi dolmuş" // veya "Geçersiz JWT imzası"
}
```

### 👥 Varsayılan Kullanıcılar

Uygulama başlatıldığında otomatik olarak oluşturulan kullanıcılar:

1. Admin Kullanıcısı
   - Kullanıcı adı: `admin`
   - Şifre: `admin123`
   - Rol: `ROLE_ADMIN`

2. Normal Kullanıcı
   - Kullanıcı adı: `user`
   - Şifre: `user123`
   - Rol: `ROLE_USER`

### 💾 H2 Veritabanı Konsolu

H2 veritabanı yönetim arayüzüne erişmek için:

1. URL: `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:mem:testdb`
3. Kullanıcı adı: `sa`
4. Şifre: (boş bırakın)

### ⚙️ Önemli Konfigürasyonlar

```yaml
jwt:
  secret: ========================SpringBootJWT=========================
  expiration: 86400000  # 24 saat (ms)

spring:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: update
```

## 🛟 Hata Yönetimi

Proje global exception handler ile yaygın hataları yönetir:
- Kimlik doğrulama hataları
- Geçersiz token hataları
- Veritabanı hataları
- Validation hataları

## 🔍 Güvenlik Önlemleri

1. Şifreler her zaman hash'lenerek saklanır
2. JWT token'ları sınırlı süre geçerlidir
3. Hassas bilgiler token içinde saklanmaz
4. Cross-Origin Resource Sharing (CORS) yapılandırması
5. Spring Security ile güvenlik katmanı

## 🤝 Katkıda Bulunma

1. Fork'layın
2. Feature branch oluşturun
3. Değişikliklerinizi commit'leyin
4. Branch'inizi push'layın
5. Pull Request oluşturun


