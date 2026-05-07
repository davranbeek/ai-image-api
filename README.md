# 🎨 AI Image Generator — Spring Boot Backend

Stability AI orqali rasm generatsiya qiluvchi to'liq Spring Boot backend loyihasi.

---

## 📋 Texnologiyalar

| Texnologiya | Versiya | Maqsad |
|-------------|---------|--------|
| Java | 21 | Asosiy til |
| Spring Boot | 3.4.x | Framework |
| Spring Data JPA | — | Ma'lumotlar bazasi |
| Spring WebFlux | — | HTTP Client |
| PostgreSQL | 15+ | Ma'lumotlar bazasi |
| MapStruct | 1.5.5 | DTO ↔ Entity mapper |
| Lombok | — | Boilerplate kamaytirish |
| SpringDoc OpenAPI | 2.8.3 | Swagger UI |
| Gradle | 8.x | Build tool |
| Stability AI API | v2beta | Rasm generatsiya |

---

## 🗂️ Loyiha strukturasi

```
src/main/java/uz/example/imagegenerator/
├── ImageGeneratorApplication.java      # Asosiy kirish nuqtasi
│
├── controller/
│   └── ImageController.java            # REST API endpointlar
│
├── dto/
│   └── ImageDto.java                   # Request / Response / Gemini DTOlar
│
├── entity/
│   └── ImageEntity.java                # DB jadval modeli
│
├── mapper/
│   └── ImageMapper.java                # MapStruct: Entity ↔ DTO
│
├── repository/
│   └── ImageRepository.java            # JPA repository + custom querylar
│
├── service/
│   ├── ImageService.java               # Service interface
│   └── impl/
│       └── ImageServiceImpl.java       # Service implementatsiya
│
├── client/
│   └── StabilityAiClient.java          # Stability AI HTTP client
│
└── exception/
    ├── GeminiApiException.java          # API xatosi
    ├── ImageNotFoundException.java      # 404 xatosi
    └── GlobalExceptionHandler.java      # Markaziy xato boshqaruvi
```

---

## ⚙️ Sozlash

### 1. API kalit olish

**Stability AI:**
1. [platform.stability.ai](https://platform.stability.ai) ga ro'yxatdan o'ting
2. **API Keys** bo'limidan kalit oling
3. Yangi hisoblarda **$25 bepul kredit** beriladi

### 2. `application.yml` sozlash

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/image_db
    username: postgres
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

stabilityai:
  api:
    key: YOUR_STABILITY_AI_KEY       # ← shu yerga kalitni yozing

server:
  port: 8080
```

### 3. PostgreSQL ma'lumotlar bazasi yaratish

```sql
CREATE DATABASE image_db;
```

---

## 🚀 Ishga tushirish

```bash
# 1. Loyihani clone qiling
git clone https://github.com/your-username/ai-image-generator.git
cd ai-image-generator

# 2. application.yml ni sozlang
# (yuqoridagi ko'rsatmalar bo'yicha)

# 3. Ishga tushiring
./gradlew bootRun
```

Server `http://localhost:8080` da ishga tushadi.

Swagger UI: **http://localhost:8080/swagger-ui/index.html**

---

## 📡 API Endpointlar

### Rasm yaratish
```http
POST /api/v1/images/generate
Content-Type: application/json

{
  "prompt": "A beautiful sunset over mountains, cinematic, 8k",
  "sampleCount": 1,
  "aspectRatio": "16:9"
}
```

**Javob:**
```json
{
  "id": 1,
  "prompt": "A beautiful sunset over mountains, cinematic, 8k",
  "aspectRatio": "16:9",
  "sampleCount": 1,
  "imageData": "iVBORw0KGgo...",
  "status": "SUCCESS",
  "createdAt": "2026-05-06 10:30:00"
}
```

---

### Barcha rasmlar (sahifalash bilan)
```http
GET /api/v1/images?page=0&size=10&sort=createdAt,desc
```

### ID bo'yicha olish
```http
GET /api/v1/images/{id}
```

### Prompt bo'yicha qidirish
```http
GET /api/v1/images/search?keyword=sunset&page=0&size=10
```

### So'nggi 10 ta muvaffaqiyatli rasm
```http
GET /api/v1/images/recent
```

### O'chirish
```http
DELETE /api/v1/images/{id}
```

---

## 📐 AspectRatio qiymatlari

| Qiymat | O'lcham | Foydalanish |
|--------|---------|-------------|
| `1:1` | Kvadrat | Instagram post |
| `16:9` | Keng ekran | YouTube thumbnail |
| `9:16` | Vertikal | Instagram Reels, TikTok |
| `3:4` | Portret | Pinterest |
| `4:3` | Standart | Eski monitor |

---

## 🗃️ Ma'lumotlar bazasi jadvali

```sql
CREATE TABLE generated_images (
    id            BIGSERIAL PRIMARY KEY,
    prompt        VARCHAR(1000) NOT NULL,
    aspect_ratio  VARCHAR(10)   NOT NULL,
    sample_count  INTEGER       NOT NULL,
    image_data    TEXT,
    file_path     VARCHAR(500),
    status        VARCHAR(20)   NOT NULL,  -- PENDING | SUCCESS | FAILED
    error_message TEXT,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP
);
```

> `ddl-auto: update` yoqilgan bo'lsa jadval avtomatik yaratiladi.

---

## Exeptions

| HTTP Kod | Sabab | Yechim |
|----------|-------|--------|
| `400` | Validation xatosi | Request body ni tekshiring |
| `401` | API kalit noto'g'ri | Stability AI kalitini yangilang |
| `402` | Kredit tugagan | Hisobni to'ldiring |
| `404` | Rasm topilmadi | ID ni tekshiring |
| `429` | Limit oshdi | Biroz kuting |
| `500` | Server xatosi | Loglarni tekshiring |

---

## 📝 Prompt yozish bo'yicha maslahatlar

Yaxshi natija uchun promptni **inglizcha**, batafsil yozing:

```
# Yaxshi prompt namunasi
"A lone astronaut standing on Mars, red desert landscape, 
dramatic lighting, cinematic, ultra realistic, 8k, 
detailed textures, volumetric fog"

# Yomon prompt
"astronaut mars"
```

**Tavsiya etiladigan kalit so'zlar:**
- Sifat: `cinematic`, `ultra realistic`, `highly detailed`, `8k`, `4k`
- Uslub: `oil painting`, `digital art`, `watercolor`, `photorealistic`
- Yorug'lik: `dramatic lighting`, `golden hour`, `soft light`

---

## 🔧 Muammolar va yechimlar

**"Model yuklanmoqda" (503 xato)**
> Stability AI serverlari band. 10-20 soniya kuting va qayta urinib ko'ring.

**"API kalit noto'g'ri" (401 xato)**
> `application.yml` dagi `stabilityai.api.key` ni tekshiring.

**PostgreSQL ulanmaydi**
> `spring.datasource.url`, `username`, `password` ni tekshiring. `image_db` bazasi yaratilganiga ishonch hosil qiling.

**Port band**
> `server.port: 8081` ga o'zgartiring.

---

## 👨‍💻 Muallif

**Davronbek Sharipov**
- Email: davronsharipov265@email.com

---

## 📄 Litsenziya

MIT License — bepul foydalanishingiz mumkin.
