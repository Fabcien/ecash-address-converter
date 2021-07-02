eCash-address-converter
============

[![Build Status](https://travis-ci.com/Fabcien/ecash-address-converter.svg?branch=main)](https://travis-ci.com/Fabcien/ecash-address-converter)


Simple address converter from legacy to eCash and vice versa. It is fully covered by unit tests.

Usage
-----

The class `AddressConverter` is the entry point to the ecash-address-converter API, use it to convert addresses.

### Legacy -> eCash

You can convert legacy address from a `String` to the eCash format:

```java
String ecash_address = AddressConverter.legacyToECashAddress(legacy_address);
```

### eCash -> Legacy

You can convert eCash address from a `String` with format "ecash:${your_address}" to legacy format:

```java
String legacy_address = AddressConverter.eCashToLegacyAddress(ecash_address);
```

### Legacy -> BCH

You can convert legacy address from a `String` to the BCH format:

```java
String bch_address = AddressConverter.legacyToBitcoinCashAddress(legacy_address);
```

### BCH -> Legacy

You can convert BCH address from a `String` with format "bitcoincash:${your_address}" to legacy format:

```java
String legacy_address = AddressConverter.BitcoinCashToLegacyAddress(bch_address);
```

### BCH -> eCash

You can convert BCH address from a `String` with format "bitcoincash:${your_address}" to the eCash format:

```java
String ecash_address = AddressConverter.BitcoinCashToECashAddress(bch_address);
```

### eCash -> BCH

You can convert eCash address from a `String` with format "ecash:${your_address}" to BCH format:

```java
String bch_address = AddressConverter.eCashToBitcoinCashAddress(bch_address);
```

### Example:

```java
String legacy_address = "18uzj5qpkmg88uF3R4jKTQRVV3NiQ5SBPf";
String ecash_address = AddressConverter.toECashAddress(legacy_address);
System.out.println(ecash_address); // output: ecash:qptvav58e40tcrcwuvufr94u7enkjk6s2qxtsl8nr9

String ecash_address = "ecash:qptvav58e40tcrcwuvufr94u7enkjk6s2qxtsl8nr9";
String legacy_address = AddressConverter.toLegacyAddress(ecash_address);
System.out.println(legacy_address); // output: 18uzj5qpkmg88uF3R4jKTQRVV3NiQ5SBPf
```

Packaging
---------

To build a `.jar` from the project:

```
mvn package
```

Test
----

To run the unit tests:

```
mvn test
```

Include
-------

### Maven

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```
```xml
<dependency>
  <groupId>com.github.fabcien</groupId>
  <artifactId>ecash-address-converter</artifactId>
  <version>1.0</version>
</dependency>
```

### Gradle

```gradle
allprojects {
  repositories {
  ...
  maven { url 'https://jitpack.io' }
  }
}
  
dependencies {
  implementation 'com.github.fabcien:ecash-address-converter:1.0'
}
```
