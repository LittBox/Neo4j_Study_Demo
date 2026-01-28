import scrapy
from disease_spider.items import DiseaseItem
import requests
import time
import sys
import os

# 添加当前目录到系统路径，确保可以导入get_cookie
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

try:
    from get_cookie import get_baidu_cookies
    HAS_COOKIE_MODULE = True
except ImportError:
    print("警告: 无法导入 get_cookie 模块，将使用固定cookies")
    HAS_COOKIE_MODULE = False


class BaikeFinalSpider(scrapy.Spider):
    name = "baike_disease"  # 改为baike_disease
    allowed_domains = ["baike.baidu.com"]
    DISEASE_LIST_PATH = "D:\\KnoledgeMap\\disease_spider\\disease_list.txt"
    
    # 备用cookies（如果自动获取失败时使用）
    BACKUP_COOKIES = {
        'BAIDUID': 'F8147D87A7D29CECA581A5F9431B656D:FG=1',
        'BIDUPSID': 'F8147D87A7D29CECE12A12254CFBE4CA',
        'PSTM': '1769611054',
        'BAIDUID_BFESS': 'F8147D87A7D29CECA581A5F9431B656D:FG=1',
        'BD_HOME': '1',
    }
    
    # 存储当前使用的cookies
    current_cookies = None
    
    def __init__(self, *args, **kwargs):
        super(BaikeFinalSpider, self).__init__(*args, **kwargs)
        # 初始化时获取cookies
        self.get_fresh_cookies()
    
    def get_fresh_cookies(self, max_retries=2):
        """获取新的cookies"""
        self.logger.info("正在获取新的百度cookies...")
        
        if HAS_COOKIE_MODULE:
            for i in range(max_retries):
                try:
                    self.logger.info(f"尝试获取cookies (第{i+1}次)...")
                    cookies = get_baidu_cookies()
                    if cookies:
                        self.current_cookies = cookies
                        self.logger.info(f"成功获取cookies: {list(cookies.keys())}")
                        return
                    else:
                        self.logger.warning(f"第{i+1}次获取cookies失败")
                        time.sleep(2)
                except Exception as e:
                    self.logger.error(f"获取cookies时出错: {e}")
        
        # 如果自动获取失败，使用备用cookies
        self.logger.warning("自动获取cookies失败，使用备用cookies")
        self.current_cookies = self.BACKUP_COOKIES.copy()
    
    def start_requests(self):
        """读取疾病列表，生成请求"""
        # 确保有可用的cookies
        if not self.current_cookies:
            self.get_fresh_cookies()
        
        try:
            with open(self.DISEASE_LIST_PATH, "r", encoding="utf-8") as f:
                disease_list = [line.strip() for line in f if line.strip()]
            self.logger.info(f"成功读取疾病列表，共{len(disease_list)}个疾病")
        except FileNotFoundError:
            self.logger.error(f"未找到疾病列表文件，使用兜底列表")
            disease_list = ["高血压", "糖尿病"]

        # 疾病到ID的映射（更新正确的ID）
        disease_id_map = {
            "高血压": "195863",
            "糖尿病": "100403",
            "感冒": "502565",
            "冠心病": "547914",  # 修正ID
            "肺炎": "1083485",
            "胃溃疡": "533464",  # 修正ID
            "哮喘": "532",  # 这个可能需要验证
            "类风湿关节炎": "194927",
            "痛风": "421435",
            "脑卒中": "2204237",
        }

        for disease in disease_list:
            # 如果有ID映射，使用带ID的URL
            disease_id = disease_id_map.get(disease)
            if disease_id:
                url = f"https://baike.baidu.com/item/{disease}/{disease_id}"
            else:
                # 否则使用普通URL
                url = f"https://baike.baidu.com/item/{disease}"
            
            headers = {
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36',
                'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
                'Referer': 'https://www.baidu.com/',
            }
            
            yield scrapy.Request(
                url=url,
                callback=self.parse,
                meta={"disease_name": disease, "retry_count": 0},
                headers=headers,
                cookies=self.current_cookies,
                dont_filter=True,
                errback=self.errback_handler
            )
    
    def errback_handler(self, failure):
        """错误处理"""
        request = failure.request
        disease_name = request.meta.get("disease_name", "未知疾病")
        retry_count = request.meta.get("retry_count", 0)
        
        self.logger.error(f"请求失败: {request.url} - {failure.value}")
        
        # 如果是cookies问题，尝试重新获取
        if retry_count < 1 and hasattr(failure.value, 'response'):
            response = failure.value.response
            if response and response.status in [403, 429, 503]:
                self.logger.warning(f"疑似cookies失效，尝试重新获取cookies...")
                self.get_fresh_cookies()
                
                # 重新发起请求
                new_meta = request.meta.copy()
                new_meta["retry_count"] = retry_count + 1
                
                yield scrapy.Request(
                    url=request.url,
                    callback=self.parse,
                    meta=new_meta,
                    headers=request.headers,
                    cookies=self.current_cookies,
                    dont_filter=True
                )

    def parse(self, response):
        """解析百度百科页面"""
        disease_name = response.meta.get("disease_name", "未知疾病")
        retry_count = response.meta.get("retry_count", 0)
        
        if response.status != 200:
            self.logger.warning(f"非200响应: {response.status} for {disease_name}")
            
            # 如果是认证问题且尚未重试，尝试重新获取cookies
            if response.status in [403, 429] and retry_count < 1:
                self.logger.warning(f"疑似cookies失效，尝试重新获取cookies并重试...")
                self.get_fresh_cookies()
                
                # 重新发起请求
                new_meta = response.meta.copy()
                new_meta["retry_count"] = retry_count + 1
                
                yield scrapy.Request(
                    url=response.url,
                    callback=self.parse,
                    meta=new_meta,
                    headers=response.request.headers,
                    cookies=self.current_cookies,
                    dont_filter=True,
                    errback=self.errback_handler
                )
            return
        
        if len(response.text) < 10000:
            self.logger.warning(f"响应过短: {len(response.text)} 字符 for {disease_name}")
            
            # 如果页面太短，可能是cookies失效，尝试重新获取
            if len(response.text) < 5000 and retry_count < 1:
                self.logger.warning(f"页面内容异常，可能cookies失效，尝试重新获取cookies...")
                self.get_fresh_cookies()
                
                # 重新发起请求
                new_meta = response.meta.copy()
                new_meta["retry_count"] = retry_count + 1
                
                yield scrapy.Request(
                    url=response.url,
                    callback=self.parse,
                    meta=new_meta,
                    headers=response.request.headers,
                    cookies=self.current_cookies,
                    dont_filter=True,
                    errback=self.errback_handler
                )
                return
            
        self.logger.info(f"===== 开始解析: {disease_name} =====")
        self.logger.info(f"页面大小: {len(response.text)} 字符")
        self.logger.info(f"页面URL: {response.url}")
        if retry_count > 0:
            self.logger.info(f"重试次数: {retry_count}")
        
        # 保存页面片段用于调试
        debug_file = f"debug_{disease_name}_{int(time.time())}.txt"
        with open(debug_file, "w", encoding="utf-8") as f:
            f.write(response.text[:2000])
        self.logger.info(f"保存调试信息到: {debug_file}")
        
        item = DiseaseItem()
        item["中文名"] = response.css("h1::text").get(default=disease_name).strip()
        
        # 初始化字段
        item["就诊科室"] = ""
        item["常见病因"] = ""
        item["常见症状"] = ""
        item["相关药物"] = ""
        
        # ===== 核心：多种方法提取基础信息 =====
        
        # 方法1：标准的基础信息栏
        basic_info = response.css(".basic-info")
        
        # 方法2：备用选择器
        if not basic_info:
            basic_info = response.css(".basicInfo")
        
        # 方法3：其他可能的选择器
        if not basic_info:
            basic_info = response.css("div[class*='basic']")
        
        if basic_info:
            self.logger.info("✓ 找到基础信息容器")
            
            # 方法A：使用dt/dd结构
            dt_elements = basic_info.css("dt")
            if dt_elements:
                self.logger.info(f"找到 {len(dt_elements)} 个dt元素")
                
                for i, dt in enumerate(dt_elements):
                    key = dt.css("::text").get(default="").strip()
                    if key:
                        # 获取对应的dd
                        dd = dt.xpath("following-sibling::dd[1]")
                        value = "".join(dd.css("*::text").extract()).strip()
                        
                        if value:
                            self.logger.debug(f"  [{i+1}] {key}: {value[:30]}...")
                            self.process_field(item, key, value)
            
            # 方法B：如果没有dt/dd，尝试其他结构
            else:
                # 尝试查找基本信息项
                info_items = basic_info.css(".basicInfo-item, .info-item, [class*='item']")
                if info_items:
                    self.logger.info(f"找到 {len(info_items)} 个信息项")
                    
                    for item_elem in info_items:
                        key = item_elem.css(".name::text, dt::text, .title::text").get(default="").strip()
                        value = "".join(item_elem.css(".value::text, dd::text, .content::text, *::text").extract()).strip()
                        
                        if key and value:
                            self.process_field(item, key, value)
                
                # 方法C：直接提取所有文本，然后解析
                else:
                    text_content = basic_info.css("*::text").getall()
                    text = " ".join([t.strip() for t in text_content if t.strip()])
                    self.logger.info(f"基础信息文本长度: {len(text)}")
                    
                    # 简单关键词匹配
                    if "就诊科室" in text:
                        # 提取就诊科室相关内容
                        start = text.find("就诊科室")
                        if start != -1:
                            end = text.find("\n", start)
                            if end != -1 and end - start < 100:
                                item["就诊科室"] = text[start:end].strip()
        
        else:
            self.logger.warning("✗ 未找到基础信息容器")
            
            # 在页面中搜索关键词
            page_text = " ".join(response.css("body ::text").extract())
            
            # 直接搜索目标字段
            search_terms = {
                "就诊科室": ["就诊科室", "科室"],
                "常见病因": ["病因", "发病原因"],
                "常见症状": ["症状", "临床表现"],
                "相关药物": ["药物", "用药", "治疗药物"],
            }
            
            for field, terms in search_terms.items():
                for term in terms:
                    if term in page_text and not item[field]:
                        idx = page_text.find(term)
                        if idx != -1:
                            # 提取合理长度的内容
                            end_idx = page_text.find("。", idx)
                            if end_idx != -1 and end_idx - idx < 150:
                                item[field] = page_text[idx:end_idx+1].strip()
                                break
        
        # ===== 清理和验证提取的内容 =====
        for field in ["就诊科室", "常见病因", "常见症状", "相关药物"]:
            if item[field]:
                # 清理换行和多余空格
                item[field] = " ".join(item[field].split())
                # 限制长度
                if len(item[field]) > 200:
                    item[field] = item[field][:197] + "..."
        
        # ===== 输出结果 =====
        extracted_fields = [k for k, v in item.items() if v and k != "中文名"]
        self.logger.info(f"解析完成：提取到 {len(extracted_fields)} 个字段")
        
        for field in extracted_fields:
            self.logger.info(f"  {field}: {item[field][:50]}...")
        
        yield item
    
    def process_field(self, item, key, value):
        """处理提取到的字段"""
        key_clean = key.replace(" ", "").replace("\n", "").replace("\t", "")
        
        # 就诊科室
        if any(k in key_clean for k in ["就诊科室", "科室", "科别"]):
            if not item["就诊科室"]:
                item["就诊科室"] = value
                self.logger.info(f"  匹配到就诊科室: {value[:50]}...")
        
        # 常见病因
        elif any(k in key_clean for k in ["病因", "发病原因", "致病原因"]):
            if not item["常见病因"]:
                item["常见病因"] = value
                self.logger.info(f"  匹配到常见病因: {value[:50]}...")
        
        # 常见症状
        elif any(k in key_clean for k in ["症状", "临床表现", "主要症状"]):
            if not item["常见症状"]:
                item["常见症状"] = value
                self.logger.info(f"  匹配到常见症状: {value[:50]}...")
        
        # 相关药物
        elif any(k in key_clean for k in ["药物", "用药", "治疗药物", "常用药物"]):
            if not item["相关药物"]:
                item["相关药物"] = value
                self.logger.info(f"  匹配到相关药物: {value[:50]}...")


# 运行爬虫的示例代码
if __name__ == "__main__":
    # 测试cookies获取
    if HAS_COOKIE_MODULE:
        print("测试获取cookies...")
        try:
            cookies = get_baidu_cookies()
            if cookies:
                print(f"成功获取cookies: {cookies}")
            else:
                print("获取cookies失败")
        except Exception as e:
            print(f"获取cookies时出错: {e}")
    else:
        print("无法导入get_cookie模块")