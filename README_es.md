<a href="README.md">
<img align="right" src="src/main/resources/img/countries_flags/GB.png">
</a>

# UBUMonitor
[![GitHub release](https://img.shields.io/github/release/CatalinMarc/UBUMonitor.svg)](https://github.com/CatalinMarc/UBUMonitor/releases/)
[![GitHub license](https://img.shields.io/github/license/CatalinMarc/UBUMonitor)](https://github.com/CatalinMarc/UBUMonitor/blob/master/LICENSE)

## Release 
La release de la aplicación está disponible: [Aquí](https://github.com/CatalinMarc/UBUMonitor/releases)

## Monitorización de alumnos en la plataforma Moodle
### Descripción:

Herramienta de visualización de calificaciones y registros (logs) del curso en diferentes tipos de gráficas.

Esta aplicación está dirigida a docentes que usen los servicios de Moodle.

Esta proyecto es una extensión del módulo de clustering de la aplicación [UBUMonitor Clustering](https://github.com/xjx1001/UBUMonitor) de [Xing Long Ji](https://github.com/xjx1001).
## Citar trabajo
[Citar este trabajo](https://www.mdpi.com/2079-9292/11/6/954#cite)

## Dependencias:
La aplicación requiere **Java 8**.
Las librerías externas de **Java** están incluidas en la carpeta [lib](lib), de **JavaScript** en [src/main/resources/graphics/lib](src/main/resources/graphics/lib) y un pack de iconos.

### Dependencias de Java:
* [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)
  * Versión: **1.8**
  * [Github](https://github.com/apache/commons-csv)
  * [Maven Repository](https://mvnrepository.com/artifact/org.apache.commons/commons-csv)
* [Apache Commons Math](https://commons.apache.org/proper/commons-math/)
  * Versión: **3.6.1**
  * [Github](https://github.com/apache/commons-math)
  * [Maven Repository](https://mvnrepository.com/artifact/org.apache.commons/commons-math3)
* [Apache Lucene](https://lucene.apache.org/)
  * Versión: **8.6.2**
  * [Github](https://github.com/apache/lucene-solr)
  * [Maven Repository](https://mvnrepository.com/artifact/org.apache.lucene/lucene-core)
* [Apache POI](https://poi.apache.org/)
  * Versión: **4.1.1**
  * [Github](https://github.com/apache/poi)
  * [Maven Repository](https://mvnrepository.com/artifact/org.apache.poi/poi)  
* [CommonMark](https://commonmark.org/)
  * Versión: **0.15.1**
  * [Github](https://github.com/atlassian/commonmark-java)
  * [Maven Repository](https://mvnrepository.com/artifact/com.atlassian.commonmark/commonmark)    
* **ControlsFX**
  * Versión: **8.40.17**
  * [Github](https://github.com/controlsfx/controlsfx)
  * [Maven Repository](https://mvnrepository.com/artifact/org.controlsfx/controlsfx) 
* **JSON In Java**
  * Versión: **20190722**
  * [Github](https://github.com/stleary/JSON-java)
  * [Maven Repository](https://mvnrepository.com/artifact/org.json/json)
* [JSoup Java HTML Parser](https://jsoup.org/)
  * Versión: **1.12.1**
  * [Github](https://github.com/jhy/jsoup)
  * [Maven Repository](https://mvnrepository.com/artifact/org.jsoup/jsoup/1.11.3)
* **Kumo**
  * Versión: **1.27**
  * [Github](https://github.com/kennycason/kumo)
  * [Maven Repository](https://mvnrepository.com/artifact/com.kennycason/kumo-core)  
* [OkHttp](https://square.github.io/okhttp/)
  * Versión: **4.4.1**
  * [Github](https://github.com/square/okhttp/)
  * [Maven Repository](https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp)
* [OpenCSV](http://opencsv.sourceforge.net/)
  * Versión: **4.6**
  * [GitHub](https://github.com/jlawrie/opencsv)
  * [Maven Repository](https://mvnrepository.com/artifact/com.opencsv/opencsv/4.6)
* [SLF4J API Module](https://www.slf4j.org/)
  * Versión: **1.7.26**
  * [Github](https://github.com/qos-ch/slf4j)
  * [Maven Repository](https://mvnrepository.com/artifact/org.slf4j/slf4j-api)
* [SLF4J LOG4J 12 Binding](https://www.slf4j.org/)
  * Versión: **1.7.26**
  * [Github](https://github.com/qos-ch/slf4j/tree/master/slf4j-log4j12)
  * [Maven Repository](https://mvnrepository.com/artifact/org.slf4j/slf4j-log4j12)
* **Smile**
  * Versión: **2.4.0**
  * [Github](https://github.com/haifengl/smile)
  * [Maven Repository](https://mvnrepository.com/artifact/com.github.haifengl/smile-core)  
* **T-SNE-Java**
  * Versión: **2.5.0**
  * [Github](https://github.com/lejon/T-SNE-Java)
  * [Maven Repository](https://mvnrepository.com/artifact/com.github.lejon.T-SNE-Java/tsne)
* [ThreeTen Extra](https://www.threeten.org/threeten-extra/)
  * Versión: **1.5.0**
  * [Github](https://github.com/ThreeTen/threeten-extra)
  * [Maven Repository](https://mvnrepository.com/artifact/org.threeten/threeten-extra)  
  
### Dependencias JavaScript:
* [ApexCharts](https://apexcharts.com/)
  * Versión: **3.19.2**
  * [Github](https://github.com/apexcharts/apexcharts.js)
  * [JSDELIVR Repository](https://www.jsdelivr.com/package/npm/apexcharts)
* [Chart.js](https://www.chartjs.org/)
  * Versión: **2.9.3**
  * [Github](https://github.com/chartjs/Chart.js)
  * [CDNJS Repository](https://cdnjs.com/libraries/Chart.js/)
* **Chart.js Box and Violin Plot**
  * Versión: **2.3.0**
  * [Github](https://github.com/datavisyn/chartjs-chart-box-and-violin-plot)
  * [JSDELIVR](https://www.jsdelivr.com/package/npm/chartjs-chart-box-and-violin-plot)
* **color-hash**
  * Versión: **1.0.3**
  * [Github](https://github.com/zenozeng/color-hash)
  * [jsDelivr Repository](https://www.jsdelivr.com/package/npm/color-hash)
* [html2canvas](https://html2canvas.hertzen.com/)
  * Versión: **1.0.0-rc.5**
  * [Github](https://github.com/niklasvh/html2canvas/)
* [Plotly](https://plotly.com/javascript/)
  * Versión: **2.6.4**
  * [Github](https://github.com/plotly/plotly.js)
  * [CDNJS](https://cdnjs.com/libraries/plotly.js)
* [Tabulator](http://tabulator.info/)
  * Versión: **4.9.3**
  * [Github](https://github.com/olifolkerd/tabulator)
  * [CDNJS](https://cdnjs.com/libraries/tabulator)
* [Vis Network](https://visjs.github.io/vis-network/docs/network/)
  * Versión: **8.2.0**
  * [Github](https://github.com/visjs/vis-network)
  * [UNPKG](https://unpkg.com/browse/vis-network@8.2.0/standalone/umd/)
* [Vis Timeline](https://visjs.github.io/vis-timeline/docs/timeline/)
  * Versión: **7.3.7**
  * [Github](https://github.com/visjs/vis-network)
  * [UNPKG](https://unpkg.com/browse/vis-timeline@7.3.7/standalone/umd/)
  
### Pack de iconos:
* [Font Awesome](https://fontawesome.com/)
  * Versión: **4.7.0**
  * [Github](https://github.com/FortAwesome/Font-Awesome/)
  * [fa2png](http://fa2png.io/r/font-awesome/)

## Autor

-   Ionut Catalin Marc

## Tutor
-   Raúl Marticorena Sánchez

## Licencia
Este proyecto está licenciado bajo la licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.
