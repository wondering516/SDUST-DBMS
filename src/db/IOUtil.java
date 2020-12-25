package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/***
 *
 *
 * @Author: CoffeeChicken
 * @Date: 2020/12/25 16:29
 * @Usage:
 *
*/

public class IOUtil {

	public static List<String> readFile(File temp) {
		BufferedReader br = null;
		List<String> list = new ArrayList<String>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(temp), "GBK"));
			String line = null;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return list;
	}
	


}
