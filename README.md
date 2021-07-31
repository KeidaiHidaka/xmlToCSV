# xmlToCSV

好きな単語で検索 & csv出力ツール

1.はじめに<br>
昨今話題になっている、コロナウィルス、緊急事態宣言等。
それらの気になるワードを含むニュースだけを抽出して、csvファイルに出力するツールを作りました。<br>
<br>
<br>

2.プログラムで使用するデータとデータ記述言語<br>
指定したxmlを、Xpathを用いてRSSのitem要素まで読み込み、title　link　description要素等を抜き出してコンソールに出力したり、csvファイルに出力したりしている。<br>
<br>
<br>
3.プログラムの設計と実装<br>
3.1実現した機能<br>
ユーザーが指定したxml(ニュース系のサイトのものだと良い)から、ユーザーの指定した単語(例：コロナ　緊急事態宣言　逮捕　等)を含んでいる要素を取り出し、コンソールに出力、csvファイルにも出力する。<br>
具体的な使い方としては、ExtractAnyWordFromXmlAndOutputToCsv.javaの171行目のwordに調べたい文字列、下のurl、url2に調べたいxml、siteName、siteName2に各自選んだサイトのわかりやすい題名(csvファイルの名前に使用)をいれる。<br>
実行するとコンソールとcsvファイルに出力される。ファイル名は「” ユーザーの指定した単語””サイトの題名１”And”サイトの題名２”出力した時の年月日時刻.csv」となる。<br>
URLは末尾がxml(RSS)でないと機能しない。<br>

3.2	システムが利用するリソース<br>
ユーザーが指定する者のため今回は例とする<br>

名称: Yahoo!ニュース・トピックス - 国内<br>
概要: Yahoo! JAPANのニュース・トピックスで取り上げている最新の見出しを提供しています。<br>
利用目的:知りたいニュースの抽出<br>
URL: https://news.yahoo.co.jp/topics/domestic?source=rss<br>
形式: RSS<br>
<br>
名称: NHKニュース<br>
概要: 日本放送協会 NHKニュース<br>
利用目的:知りたいニュースの抽出<br>
URL: http://www3.nhk.or.jp/news/<br>
形式: RSS<br>
<br>

3.3	プログラムの構成<br>
ExtractAnyWordFromXmlAndOutputToCsv.javaについて、DocumentでDOM ツリーの構築、ExtractAnyWordFromXmlAndOutputToCsvではFeedのURLをうけとるコンストラクタ、getItemListではユーザ指定の文字列を含むitem要素のリストを返し、mainでコンソールとcsvファイルの出力を行う。<br>
Item.javaについて、toStringでこのitem要素の文字列表現を返す。コンソール出力用。<br>
<br>
<br>
3.4	データ構造とアルゴリズム<br>
getItemListでtitleかdescription要素にユーザの指定の文字列が含まれている場合のみitemListに追加するようにしたことで、ほかの不要なニュースを排除した。<br>
<br>

3.5	その他の工夫点<br>
コンソールに出力されるだけでは不便だと思ったため、csvファイルに出力することでデータをより扱いやすくした。<br>
単にcsvファイルをエクセルで開くと文字化けしてしまうため、プログラムのほうから文字コードを指定したが、どうやらエクセルのほうに問題があるようで、正常にデータを見るにはエクセル→「データ」→「テキストまたはCSVから」でCSVファイルを指定してもらい、文字コードをUTF-8に変更する必要がある。<br>
googleスプレッドシートで「ファイル」→「開く」からCSVファイルを指定すると文字化けせずに簡単に読み込むことができる。<br>
<br>
<br>
4.	実験<br>
4.1	実験条件<br>
ExtractAnyWordFromXmlAndOutputToCsv.javaのmainの中に、調べたい文字列、url等を入力。<br>
今回の例として、<br>

文字列：コロナ<br>
URL ：https://news.yahoo.co.jp/rss/categories/domestic.xml<br>
URL2：https://www.nhk.or.jp/rss/news/cat0.xml<br>
siteName：YahooNews<br>
siteName2: NHK<br>
とする。<br>



4.2	実験結果<br>

 
出力されたCSVファイル例を挙げた<br>
 <br><br>
5.	考察<br>
5.1	機能<br>
知りたい情報を抜き出してくれる実用的なツールを作ることができた。<br>
改良点としては、３つ以上のサイトを指定しできるようにしたり、コンソール画面で文字列やURLを指定したりできるようにすることが挙げられる。<br>

5.2	実現方法<br>
効率に関して、getItemListにてtitle、link等にitemNodeから入力した後に、<br>
titleかdescriptionに指定のワードが含まれているもののみitemListに追加しているため、無駄な入力が行われることがあるため、効率は悪いように思える。<br>
