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

## `Product` jako obiekt niemutowalny (immutable)
`Product` jest niemutowalny, a “cena po rabacie” (`discountPrice`) jest realizowana przez tworzenie nowej instancji (`withDiscountPrice(...)`).
Dzięki temu unikamy błędów wynikających z ubocznych efektów (np. wielokrotne przeliczenie koszyka) i łatwiej testujemy zachowanie.

## Uwaga o `double`
Zgodnie z treścią zadania ceny są w `double` (uproszczenie).
W realnych systemach finansowych używa się `BigDecimal` albo groszy w `long`.

