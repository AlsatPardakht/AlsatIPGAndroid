<p align="center">
  <a href="" rel="noopener">
 <img width=200px height=200px src="./logo.png" alt="Project logo"></a>
</p>

<h3 align="center">Alsat IPG Android</h3>

<div align="center">

[![Status](https://img.shields.io/badge/status-active-success.svg)]()
[![GitHub Issues](https://img.shields.io/github/issues/AlsatPardakht/AlsatIPGAndroid.svg)](https://github.com/AlsatPardakht/AlsatIPGAndroid/issues)
[![GitHub Pull Requests](https://img.shields.io/github/issues-pr/AlsatPardakht/AlsatIPGAndroid.svg)](https://github.com/AlsatPardakht/AlsatIPGAndroid/pulls)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](/LICENSE)

</div>

---

<p align="center">با استفاده از این کتابخانه میتوانید اپلیکیشنتون رو به شبکه پرداخت آل‌سات پرداخت وصل کنید و به راحتی محصولات خودتون رو داخل اپلیکیشن بفروشید
    <br> 
</p>

<div dir="rtl">

## 📝 فهرست

- [درباره](#about)
- [نحوه استفاده](#usage)
- [شروع به کار](#getting_started)
- [ساخته شده با استفاده از](#built_using)

## 🧐 درباره <a name = "about"></a>
<p dir="rtl">
این کتابخانه برای آسان سازی ارتباط با api های سرویس IPG آل سات پرداخت است و لیست تمامی api ها در لینک زیر موجود هستند  :
</p>
<a href="https://www.alsatpardakht.com/TechnicalDocumentation/191">🌐 مستندات فنی IPG های آل سات پرداخت</a><br>

## 🏁 شروع به کار <a name = "getting_started"></a>

برای شروع کافی است مراحل زیر را با دقت  روی پروژه خود انجام بدهید .
<br>


### مرحله اول :

داخل فایل AndroidManifest.xml اپلیکیشن خود دسترسی به اینترنت را اضافه کنید 👇
</div>

```XML
<manifest xmlns:android="..."
    package="...">

    <uses-permission android:name="android.permission.INTERNET" />

    ...

</manifest>
```
<div dir="rtl">

### مرحله دوم :
داخل فایل build.gradle ماژول app خود dependency زیر را اضافه کنید 👇

</div>

<details open>
    <summary>Gradle Groovy DSL</summary>

```gradle
dependencies {

    ...

    implementation 'com.alsatpardakht:alsatipgcore:1.0.0'
    implementation 'com.alsatpardakht:alsatipgandroid:1.0.0'

}
```

</details>

<details>
    <summary>Gradle Kotlin DSL</summary>

```Kotlin
implementation("com.alsatpardakht:alsatipgcore:1.0.0")
implementation("com.alsatpardakht:alsatipgandroid:1.0.0")
```

</details>

<details>
    <summary>Apache Maven</summary>

```XML
<dependency>
  <groupId>com.alsatpardakht</groupId>
  <artifactId>alsatipgcore</artifactId>
  <version>1.0.0</version>
</dependency>

<dependency>
  <groupId>com.alsatpardakht</groupId>
  <artifactId>alsatipgandroid</artifactId>
  <version>1.0.0</version>
</dependency>
```

</details>

<div dir="rtl">

## 🎈 نحوه استفاده <a name="usage"></a>

دو نمونه از نحوه استفاده از این کتابخانه در لینک های زیر موجود است :

- <a href="https://www.github.com/AlsatPardakht/AlsatIPGAndroidKotlinExample">نمونه استفاده از این کتابخانه در Kotlin</a><br>
- <a href="https://www.github.com/AlsatPardakht/AlsatIPGAndroidJavaExample">نمونه استفاده از این کتابخانه در Java</a>

## ⛏️ ساخته شده با استفاده از  <a name = "built_using"></a>

</div>

- [Kotlin](https://kotlinlang.org/) - programming language
- [Ktor](https://ktor.io/) - HTTP client
- [Coroutines](https://expressjs.com/) - asynchronous & concurrency
- [kotlinx-serialization-json](https://vuejs.org/) - serialization & deserialization