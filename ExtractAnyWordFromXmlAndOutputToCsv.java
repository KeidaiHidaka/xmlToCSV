
package jp.ac.dendai.im.web.search.google_search;



import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

/**
 * フィード
 */
public class ExtractAnyWordFromXmlAndOutputToCsv {
	/** フィードの URL */
	private String urlString;
	/** DOMツリー */
	private Document document;
	/**
	 *  コンストラクタ
	 *  @param urlString FeedのURL
	 */
	public ExtractAnyWordFromXmlAndOutputToCsv(String urlString) {
		// TLS v1.2 の有効化 (Java 8 以降では指定不要)
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		this.urlString = urlString;
		try {
			// InputStreamの用意
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			// DOMツリーの構築
			document = this.buildDocument(inputStream, "utf-8");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * フィードの URLを返す
	 * @return URL
	 */
	public String getURLString() {
		return urlString;
	}
	/**
	 * item 要素のリストを返す
	 * @return item要素の ArrayList
	 */
	public ArrayList<Item> getItemList(String word) {
		ArrayList<Item> itemList = new ArrayList<Item>();
		//ArrayList<Item> itemList2 = new ArrayList<Item>();
		try {
			// XPath の表現を扱う XPath オブジェクトを生成
			XPath xPath = XPathFactory.newInstance().newXPath();
		    // item要素のリストを得る (RSS 2.0のパス)
			NodeList itemNodeList = (NodeList)xPath.evaluate("/rss/channel/item",
					document, XPathConstants.NODESET);

			/*
			 * "/rss/channel/item",
			 * "/rss/channel/item[contains(text(), 'コロナ')]",
			 * これは無理だった
			 */


			// RSS 1.0
		    if(itemNodeList.getLength() == 0) {
			itemNodeList = (NodeList)xPath.evaluate("/RDF/item",
						document, XPathConstants.NODESET);
		    }



			for(int i = 0; i < itemNodeList.getLength(); i++) {
				Node itemNode= itemNodeList.item(i);

				/*Node itemNode= itemNodeList.item(i);
				 * .contains("コロナ")
				 * これも無理だった
				 */


				String title = xPath.evaluate("title", itemNode);
				String link = xPath.evaluate("link", itemNode);
				String description = xPath.evaluate("description", itemNode).replace("\r\n", "").replace("\n", "").replace("　", "");
				String pubDate = xPath.evaluate("pubDate", itemNode);
				String comments = xPath.evaluate("comments", itemNode);

				//titleかdescriptionに特定の文字列があればitemListに追加
				//ここで初めて成功

				if(title.contains(word)||description.contains(word)) {
					itemList.add(new Item(title, link, description,pubDate,comments));
				}

			}


		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return itemList;
	}
	/**
	 * DOM ツリーの構築
	 * @param inputStream XMLで記述されたテキストを InputStream にしたもの
	 * @param encoding テキストの文字コード
	 * @return 文書全体の DOM ツリー
	 */
	public Document buildDocument(InputStream inputStream, String encoding) {
		Document document = null;
		try {
			// DOM実装(implementation)の用意 (Load and Save用)
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			DOMImplementationLS implementation = (DOMImplementationLS)registry.getDOMImplementation("XML 1.0");
			// 読み込み対象の用意
			LSInput input = implementation.createLSInput();
			input.setByteStream(inputStream);
			input.setEncoding(encoding);
			// 構文解析器(parser)の用意
			LSParser parser = implementation.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
			parser.getDomConfig().setParameter("namespaces", false);
			// DOMの構築
			document = parser.parse(input);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

	/** 動作確認用 */
	public static void main(String[] args) {



		String word = "コロナ";		//調べたい文字列　
		//例：コロナ　逮捕　緊急事態宣言

		String url = "https://news.yahoo.co.jp/rss/categories/domestic.xml";
		String url2 = "https://www.nhk.or.jp/rss/news/cat0.xml";

		String siteName = "YahooNews";//各自選んだサイトのわかりやすい題名。CSVファイルの名前に使用
		String siteName2 = "NHK";

		/*
		 * https://news.yahoo.co.jp/rss/topics/top-picks.xml	YahooNewsトピックス主要
		 * https://news.yahoo.co.jp/rss/categories/domestic.xml	YahooNewsトピックス国内
		 * https://news.yahoo.co.jp/rss/topics/world.xml		YahooNewsトピックス国際
		 * https://news.yahoo.co.jp/rss/topics/business.xml		YahooNewsトピックス経済
		 * https://news.yahoo.co.jp/rss/topics/it.xml			YahooNewsトピックスIT
		 */

		/*
		 * https://www.nhk.or.jp/rss/news/cat0.xml　NHKニュース主要ニュース
		 * https://www.nhk.or.jp/rss/news/cat1.xml　NHKニュース社会
		 */

		//https://kyoko-np.net/index.xml　虚構新聞

		//https://www.news24.jp/rss/index.rdf rdfはできなかった

		ExtractAnyWordFromXmlAndOutputToCsv feed = new ExtractAnyWordFromXmlAndOutputToCsv(url);
		ExtractAnyWordFromXmlAndOutputToCsv feed2 = new ExtractAnyWordFromXmlAndOutputToCsv(url2);

		int articleI = 0;
		int articleJ = 0;

		ArrayList<Item> itemList = feed.getItemList(word);
		ArrayList<Item> itemList2 = feed2.getItemList(word);

		PrintWriter printWriter = null;
		Date now = new Date();
		SimpleDateFormat day = new SimpleDateFormat("yyyy-MMdd-HHmm");
        String dayString = day.format(now);
	    try {
	    	printWriter = new PrintWriter(
                    new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream
                    (word + siteName + "And" + siteName2 + dayString + ".csv"),"UTF-8")));
	    	/*
	    	 *当初はFileWriterで書いていたが、
	    	 *csvファイルをExcelで開くと文字化けするため、printWriterで文字コードを指定した。
	    	 *しかしうまくいかなかった。
	    	 *調べてみたところどうやらExcelの方に問題があるようなのでこれは解決できなかった
	    	 *対処法としてはExcelのほうでUTF-8の文字コードを指定してもらうしかない
	    	 *
	    	 */

	    	printWriter.append("\n");
	    	printWriter.append("検索元URL,"+ url);
	    	printWriter.append("\n");
	    	printWriter.append("\n");


	    	for(Item item: itemList) {
	    		System.out.println(item);
	    		System.out.println();
	    		System.out.println("------------------------------------------------------------");//改行
	    		System.out.println();

	    		printWriter.append(item.getTitle());
	    		printWriter.append(",");
	    		printWriter.append(item.getLink());
	    		printWriter.append(",");
	    		printWriter.append(item.getDescription());
	    		printWriter.append(",");
	    		printWriter.append(item.getPubDate());
	    		printWriter.append(",");
	    		printWriter.append(item.getComments());
	    		printWriter.append("\n");

	    		articleI++;

	    	}
	    	printWriter.append("\n");
	    	printWriter.append("検索ヒット記事数,"+articleI);
	    	printWriter.append("\n");
	    	printWriter.append("\n");
	    	printWriter.append("-------------------------------------------------------------------------------------");
	    	printWriter.append("\n");
	    	printWriter.append("\n");
	    	printWriter.append("検索元URL,"+ url2);
	    	printWriter.append("\n");
	    	printWriter.append("\n");

	    	for(Item item: itemList2) {
	    		System.out.println(item);
	    		System.out.println();
	    		System.out.println("------------------------------------------------------------");//改行
	    		System.out.println();

	    		printWriter.append(item.getTitle());
	    		printWriter.append(",");
	    		printWriter.append(item.getLink());
	    		printWriter.append(",");
	    		printWriter.append(item.getDescription());
	    		printWriter.append(",");
	    		printWriter.append(item.getPubDate());
	    		printWriter.append(",");
	    		printWriter.append(item.getComments());
	    		printWriter.append("\n");

	    		articleJ++;
	    	}
	    	printWriter.append("\n");
	    	printWriter.append("検索ヒット記事数,"+articleJ);
	    	printWriter.append("\n");



	    	System.out.println("CSVファイル出力完了");
	    	System.out.println("ファイル名:"+ word + siteName + "And" + siteName2 + dayString + ".csv");
	    	System.out.println();
	    	System.out.println("検索元URL："+ url);
	    	System.out.println("検索ヒット記事数:"+articleI);
	    	System.out.println();
	    	System.out.println("検索元URL2："+ url2);
	    	System.out.println("検索ヒット記事数:"+articleJ);



	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	printWriter.close();
	    }
	}
}
