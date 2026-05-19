# DECISIONS

## Promocje / rabaty (wzorzec Strategy)
Promocje są zaimplementowane jako strategie `Discount` (`src/main/java/javamarkt/core/discount/Discount.java`).
Każda promocja implementuje metodę `apply(List<Product>) -> List<Product>` i może być dodana/usunięta w trakcie działania programu (dynamicznie).
To pozwala rozszerzać system o nowe typy promocji bez modyfikacji istniejących klas (OCP) i programować do interfejsu (DIP).

## Sortowanie (Comparator + możliwość podmiany)
Koszyk trzyma pole `Comparator<Product>` i domyślnie sortuje:
1) malejąco po cenie,
2) rosnąco po nazwie.
W każdej chwili można podmienić komparator metodą `setSortOrder(...)`.

## Wyszukiwanie produktów
`getCheapest()` i `getMostExpensive()` korzystają ze strumienia i funkcji `min`/`max`, więc przechodzą po liście jeden raz.
Złożoność czasowa wynosi `O(n)`, gdzie `n` oznacza liczbę produktów.

`getCheapestN(n)` i `getMostExpensiveN(n)` sortują produkty według ceny oraz nazwy, a następnie wybierają pierwsze `n` elementów.
Złożoność czasowa wynosi `O(n log n)`, ponieważ dominuje koszt sortowania.
Dla `n <= 0` zwracana jest pusta lista, a dla `n` większego od liczby produktów zwracane są wszystkie produkty w odpowiedniej kolejności.

## Algorytm promocji
Promocje są aplikowane kolejno w takiej kolejności, w jakiej zostały dodane do koszyka.
Każda promocja zwraca nową listę produktów albo niezmienioną listę wejściową, jeśli warunek promocji nie został spełniony.
`SingleUseCouponDiscount` obniża cenę tylko jednego pierwszego produktu o wskazanym kodzie w danym przeliczeniu koszyka, dzięki czemu kupon nie obejmuje wielu sztuk tego samego produktu naraz.

## Najkorzystniejsza kolejność promocji
`getBestTotalSum()` realizuje zadanie dodatkowe przez sprawdzenie wszystkich permutacji aktualnie dodanych promocji i wybranie najniższej sumy końcowej.
Dla `k` promocji złożoność wynosi `O(k! * koszt_przeliczenia_koszyka)`, więc jest to rozwiązanie poprawne dla małej liczby promocji, ale nieoptymalne dla bardzo dużej liczby reguł rabatowych.

## `Product` jako obiekt niemutowalny (immutable)
`Product` jest niemutowalny, a “cena po rabacie” (`discountPrice`) jest realizowana przez tworzenie nowej instancji (`withDiscountPrice(...)`).
Dzięki temu unikamy błędów wynikających z ubocznych efektów (np. wielokrotne przeliczenie koszyka) i łatwiej testujemy zachowanie.

## Uwaga o `double`
Zgodnie z treścią zadania ceny są w `double` (uproszczenie).
W realnych systemach finansowych używa się `BigDecimal` albo groszy w `long`.
