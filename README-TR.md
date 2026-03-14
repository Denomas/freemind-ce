# FreeMind Classic Edition (CE)

**Denomas - 2026**

[![Build Status](https://github.com/Denomas/freemind-ce/actions/workflows/build.yml/badge.svg)](https://github.com/Denomas/freemind-ce/actions)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![License: GPL v2](https://img.shields.io/badge/License-GPL%20v2-blue.svg)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)
[![OpenSSF Scorecard](https://api.scorecard.dev/projects/github.com/Denomas/freemind-ce/badge)](https://scorecard.dev/viewer/?uri=github.com/Denomas/freemind-ce)
[![OpenSSF Best Practices](https://www.bestpractices.dev/projects/12156/badge)](https://www.bestpractices.dev/projects/12156)

---

## Neden FreeMind CE?

FreeMind, bircoklarimiz icin ilk mind-mapping deneyimiydi. Yillarca fikirlerimizi, projelerimizi, hayallerimizi bu kucuk ama guclu programla organize ettik. Hizli acilirdi, sade ve odaklanmisti, gereksiz hiçbir sey yoktu. Sadece sen ve dusuncelerin.

Zaman gecti. Java surumleri degisti, isletim sistemleri guncellendi, FreeMind'in orijinal gelistirmesi durdu. Freeplane guzel bir alternatif oldu -- onu da seviyoruz. Ama FreeMind'in o ilk hali, o sadelik, o hiz... Bazi seyler unutulmuyor.

**FreeMind CE, o eski dosta yeniden hayat vermek icin dogdu.**

Orijinal FreeMind'in her satirini, her ozelligini, her ikonu koruduk. Hicbir seyi degistirmedik, sadece modernize ettik. Java 21'de calisiyor, macOS Apple Silicon'da native calisiyor, Windows 11 ve Linux'ta sorunsuz kurulup aciliyor. Gradle ile derleniyor, GitHub Actions ile otomatik paketleniyor.

Eski dostlara selam olsun. FreeMind hala burada, hala ayni, hala en hizlisi.

Bu projeyi toplulukla paylasmanin gururunu yasiyoruz.

---

## Quick Start

### Gereksinimler

- **Java 21 JDK** (Temurin, OpenJDK veya Oracle)
- **Gradle 8.6+** (wrapper dahil)

### Kurulum ve Calistirma

```bash
git clone https://github.com/Denomas/freemind-ce.git
cd freemind-ce
./gradlew build
./gradlew :freemind:run
```

### Platform Paketleri

Releases sayfasindan hazir paketleri indirebilirsiniz: [GitHub Releases](https://github.com/Denomas/freemind-ce/releases)

| Platform | Paket | Kurulum |
|----------|-------|---------|
| **macOS** | `.dmg` | Ac ve Applications'a surekle ([asagidaki nota bakin](#macos-gatekeeper)) |
| **Windows** | `.exe` | Cift tikla ve kur |
| **Linux** | `.deb` | `sudo apt install ./freemind-ce_1.1.0_amd64.deb` |

#### macOS Gatekeeper

FreeMind CE henuz Apple Developer sertifikasi ile imzalanmadigi icin macOS ilk acilista engelleyecektir. Cozum:

**Yontem 1 — Sag tiklama (onerilen):**
1. Finder'da `/Applications` klasorune gidin
2. **FreeMind-CE** uzerine sag tiklayin (veya Control+tiklama)
3. Acilan menuden **Open** secin
4. Cikan diyalogda **Open** tusuna basin
5. Bunu sadece bir kez yapmaniz yeterli — sonraki acilislar normal calisir

**Yontem 2 — Terminal:**
```bash
xattr -cr /Applications/FreeMind-CE.app
```

**Yontem 3 — Sistem Ayarlari:**
1. FreeMind-CE'yi acmayi deneyin (engellenecek)
2. **System Settings > Privacy & Security** yolunu izleyin
3. Asagi kaydigin — "FreeMind-CE was blocked" yazisini goreceksiniz
4. **Open Anyway** tusuna basin

### Kaynak Koddan Paket Olusturma

| Platform | Komut | Cikti |
|----------|-------|-------|
| **macOS** | `./gradlew :freemind:jpackageMac` | `.dmg` installer |
| **Windows** | `gradlew.bat :freemind:jpackageWin` | `.exe` installer |
| **Linux** | `./gradlew :freemind:jpackageLinux` | `.deb` package |

## Modernizasyon

```
Phase 1: Gradle Build System ✅ (Ant yerine)
Phase 2: JAXB Migration ✅ (JiBX yerine)
Phase 3: Java 21 Compatibility ✅
Phase 4: Plugin Modernization ✅
Phase 5: CI/CD & Distribution ✅
Phase 6: Standalone HTML Export ✅
Phase 7: Context Graph Plugin ✅
```

### Orijinalden Neler Degisti?

| Bilesen | Orijinal | CE |
|---------|----------|----|
| **Build** | Ant | Gradle 8.6 (Kotlin DSL) |
| **Java** | 1.6 | 21 |
| **XML Binding** | JiBX | JAXB 2.3.9 |
| **SVG/PDF** | Batik 1.6 | Batik 1.17 / FOP 2.9 |
| **Logging** | java.util.logging | SLF4J + Logback |
| **CI/CD** | Yok | GitHub Actions (multi-platform) |
| **Paketleme** | Manuel | jpackage (DMG/EXE/DEB) |
| **Encoding** | Karisik | UTF-8 everywhere |

### Neler Ayni Kaldi?

Her sey. Arayuz, ikonlar, klavye kisayollari, dosya formati (.mm), plugin sistemi, template yapisi. FreeMind'i sevdiginiz her sey oldugu gibi.

## Proje Yapisi

```
freemind-ce/
├── freemind/              # Ana uygulama modulu
│   ├── freemind/          # Core Java kaynak kodu
│   ├── accessories/       # XSLT export'lar, yardimci plugin'ler
│   ├── plugins/           # Plugin modulleri (SVG, Script, Map, Search, Help)
│   ├── images/            # Ikonlar ve goruntuler
│   ├── generated-src/     # JAXB uretilmis siniflar (dokunmayin)
│   └── build.gradle.kts   # Modul build konfigurasyonu
├── docs/                  # Proje dokumantasyonu
├── .github/workflows/     # CI/CD (build + release)
├── build.gradle.kts       # Root build konfigurasyonu
└── settings.gradle.kts    # Gradle ayarlari
```

## Gelistirme

```bash
# JAXB siniflarini yeniden uret
./gradlew :freemind:generateJaxb

# Testleri calistir
./gradlew test

# Debug modunda calistir (port 5005)
./gradlew :freemind:run --debug-jvm

# Javadoc uret
./gradlew javadoc
```

## Klavye Kisayollari (macOS)

| Islem | Kisayol |
|-------|---------|
| Yeni Harita | `⌘ N` |
| Ac | `⌘ O` |
| Kaydet | `⌘ S` |
| Cocuk Dugum Ekle | `TAB` |
| Kardes Dugum Ekle | `↵ Enter` |
| Dugumu Duzenle | `F2` |
| Katla/Ac | `SPACE` |
| Bul | `⌘ F` |
| Yakinlastir | `⌘ +` |
| Uzaklastir | `⌘ -` |

## Lisans

FreeMind CE, orijinal FreeMind ile ayni lisans altindadir: **GNU General Public License v2.0** (GPL-2.0).

```
Copyright (C) 2000-2026 Joerg Mueller, Daniel Polansky, Christian Foltin,
Dimitry Polivaev, Tolga Karatas (Denomas), and others.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
```

## Tesekkurler

- Orijinal [FreeMind](https://sourceforge.net/p/freemind/) ekibine -- bu harika programi yarattiklari icin
- SourceForge topluluğuna -- yillarca evimiz olan platform icin
- Tum FreeMind kullanicilarına -- sadik kaldiklari icin
- Apache Software Foundation'a (Batik, FOP)

## Destek

- **Issues**: [GitHub Issues](https://github.com/Denomas/freemind-ce/issues)
- **Discussions**: [GitHub Discussions](https://github.com/Denomas/freemind-ce/discussions)
- **Documentation**: [`docs/`](docs/index.md)

---

**Denomas tarafindan, FreeMind sevgisiyle.**
