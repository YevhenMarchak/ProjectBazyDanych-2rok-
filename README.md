Opis realizacji projektu
1. Cel projektu
Celem projektu „System zarządzania magazynem” będzie opracowanie relacyjnej bazy danych wspierającej obsługę procesów magazynowych w przedsiębiorstwie. System będzie umożliwiać ewidencję towarów, dostawców, klientów, pracowników, lokalizacji magazynowych oraz operacji związanych z przyjmowaniem i wydawaniem towarów.
Dzięki zastosowaniu relacyjnego modelu danych system zapewni spójność, integralność i łatwy dostęp do informacji o przepływie produktów w magazynie.

2. Etapy realizacji projektu
2.1. Projekt modelu danych
Został przygotowany model ERD (Entity-Relationship Diagram).
Zostały zidentyfikowane następujące główne encje:
•	SUPPLIERS (Dostawcy) – dane o firmach dostarczających towary,
•	CATEGORIES (Kategorie) – klasyfikacja produktów według rodzaju,
•	PRODUCTS (Towary) – informacje o produktach magazynowych,
•	LOCATIONS (Lokalizacje) – fizyczne miejsca przechowywania towarów,
•	INVENTORY (Stany magazynowe) – aktualna ilość danego towaru w konkretnej lokalizacji,
•	EMPLOYEES (Pracownicy) – osoby obsługujące operacje magazynowe,
•	CLIENTS (Klienci) – odbiorcy towarów,
•	RECEIPTS (Przyjęcia dostaw) oraz RECEIPT_DETAILS – ewidencja przyjęć produktów od dostawców,
•	SHIPMENTS (Wydania zamówień) oraz SHIPMENT_DETAILS – ewidencja wysyłek do klientów.
Zostaną również zdefiniowane relacje:
•	1:N między SUPPLIERS i PRODUCTS,
•	1:N między CATEGORIES i PRODUCTS,
•	1:N między EMPLOYEES a RECEIPTS/SHIPMENTS,
•	1:N między CLIENTS a SHIPMENTS,
•	1:N między LOCATIONS a INVENTORY,
•	N:M między RECEIPTS a PRODUCTS (tabela pośrednia RECEIPT_DETAILS),
•	N:M między SHIPMENTS a PRODUCTS (tabela pośrednia SHIPMENT_DETAILS).

3. Implementacja bazy danych
Implementacja bazy danych zostanie przeprowadzona w środowisku PostgreSQL 15 z wykorzystaniem narzędzia pgAdmin 4.
Struktura tabel zostanie zdefiniowana przy użyciu poleceń CREATE TABLE, z zastosowaniem:
•	kluczy głównych (PRIMARY KEY),
•	kluczy obcych (FOREIGN KEY) zapewniających integralność referencyjną,
•	ograniczeń NOT NULL i UNIQUE,
•	odpowiednich typów danych (np. VARCHAR, INTEGER, DATE, NUMERIC).
Dla tabel pośrednich (RECEIPT_DETAILS, SHIPMENT_DETAILS) zostaną zastosowane klucze złożone (łączące receipt_id + product_id lub shipment_id + product_id), co będzie odzwierciedlać relacje N:M.

4. Przykładowe operacje w systemie
System będzie wspierał następujące typowe operacje SQL:
•	Dodawanie nowych produktów i dostawców
(INSERT INTO PRODUCTS (...) VALUES (...);),
•	Rejestracja przyjęcia towaru
(utworzenie wpisu w tabeli RECEIPTS i odpowiednich pozycji w RECEIPT_DETAILS),
•	Aktualizacja stanów magazynowych
(UPDATE INVENTORY SET quantity = ... WHERE product_id = ...;),
•	Rejestrowanie wysyłek towarów do klientów
(INSERT INTO SHIPMENTS (...) VALUES (...);),
•	Generowanie raportów
(np. zapytania łączące PRODUCTS, INVENTORY i LOCATIONS w celu sprawdzenia dostępności towarów).
Przygotowane zapytania będą umożliwiały łatwe śledzenie ruchu towarów i szybki dostęp do informacji o dostawcach, klientach oraz stanie magazynu.

5. Wnioski i możliwości rozwoju
Zaprojektowany system w finalnej formie będzie stanowił kompletną bazę danych do obsługi operacji magazynowych.
Zastosowanie PostgreSQL pozwoli na stworzenie wydajnego, bezpiecznego i skalowalnego rozwiązania.
W przyszłości system będzie mógł zostać rozszerzony o:
•	warstwę aplikacyjną z interfejsem użytkownika (np. w technologii Python + Flask lub Java + Spring),
•	automatyczne generowanie raportów magazynowych,
•	integrację z systemami księgowymi i ERP,
•	dodanie mechanizmów kontroli uprawnień użytkowników.

