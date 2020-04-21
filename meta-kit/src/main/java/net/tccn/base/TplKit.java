package net.tccn.base;

import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.sql.SqlKit;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

/**
 * Created by liangxianyou at 2018/7/11 11:05.
 */
public class TplKit {
	private static TplKit tplKit = null;
	private static SqlKit kit = null;
	private static boolean hadParse = false;// 标记 是否已经解析

	public static TplKit use() {
		return use(false);
	}

	/**
	 * 获取单列的实例对象,
	 *
	 * @param isDev
	 *            是否开发模式, 多次调用只有第一次的 isDev生效
	 * @return
	 */
	public static TplKit use(boolean isDev) {
		synchronized (TplKit.class) {
			if (tplKit == null) {
				tplKit = new TplKit(isDev);
			}
		}
		return tplKit;
	}

	private TplKit(boolean isDev) {
		kit = new SqlKit("tplKit", isDev);
        kit.setBaseSqlTemplatePath("/");
	}

	/**
	 * [添加文件 到模板解析器]
	 * 
	 * @param tplPath
	 *            文件路劲
	 */
	public void addTpl(String tplPath) {
        kit.addSqlTemplate(tplPath);
		hadParse = false;
		System.out.println("addTpl：" + tplPath);
	}

	/**
	 * 添加文件/目录 到模板解析器
	 *
	 * @param tplFile
	 *            模板文件/目录
	 */
	public void addTpl(File tplFile) {
		addTpl(tplFile, null);
		hadParse = false;
	}

	/**
	 * 添加文件/目录 到模板解析器
	 *
	 * @param tplFile
	 *            文件/目录
	 * @param filter
	 *            文件过滤器
	 */
	public void addTpl(File tplFile, FileFilter filter) {
		if (tplFile.isFile()) {
			addTpl(tplFile.getPath());
            //addTpl(tplFile.getPath().replace(clazzRoot, "")); //以classes路径开始的路径
		} else if (tplFile.isDirectory()) {
			File[] files = tplFile.listFiles(filter);
			for (int i = 0; i < files.length; i++) {
				addTpl(files[i], filter);
			}
		}
		hadParse = false;
	}

	/**
	 * 获取模板
	 * 
	 * @param key
	 *            模板id
	 * @return
	 */
	public String getTpl(String key) {
		if (!hadParse)
			parseTpl();
		return kit.getSql(key);
	}

	/**
	 * 获取模板
	 * 
	 * @param key
	 *            模板id
	 * @param data
	 *            模板渲染数据
	 * @return
	 */
	public String getTpl(String key, Map data) {
		if (!hadParse)
			parseTpl();
		SqlPara sqlPara = kit.getSqlPara(key, data);
		return sqlPara.getSql().replaceAll("[\\s]+", " ");
	}

	public String getTpl(String key, Map data, boolean delSpace) {
		if (!hadParse)
			parseTpl();
		SqlPara sqlPara = kit.getSqlPara(key, data);

		return delSpace ? sqlPara.getSql().replaceAll("[\\s]+", " ") : sqlPara.getSql();
	}

	public String getTpl(String key, Object m) {
		if (!hadParse)
			parseTpl();
		SqlPara sqlPara = kit.getSqlPara(key, m);
		return sqlPara.getSql();
	}

	public void parseTpl() {
		kit.parseSqlTemplate();
	}

	//---------------
	private static Engine engine = Engine.use();

	public static String parseTpl(String tpl, Map data) {
		Template template = engine.getTemplateByString(tpl);
		return template.renderToString(data);
	}
}
