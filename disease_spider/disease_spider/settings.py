BOT_NAME = "disease_spider"
SPIDER_MODULES = ["disease_spider.spiders"]
NEWSPIDER_MODULE = "disease_spider.spiders"

# 核心配置
ROBOTSTXT_OBEY = False
DOWNLOAD_DELAY = 2  # 减少延迟，因为cookies有效
CONCURRENT_REQUESTS = 1
CONCURRENT_REQUESTS_PER_DOMAIN = 1

# 日志和编码
LOG_LEVEL = "INFO"
FEED_EXPORT_ENCODING = "utf-8"

# 启用HTTP压缩
HTTPCOMPRESSION_ENABLED = True

# 简单的请求头
DEFAULT_REQUEST_HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Accept-Language": "zh-CN,zh;q=0.9",
    "Accept-Encoding": "gzip, deflate",
    "Referer": "https://www.baidu.com/",
}

# 启用数据管道
ITEM_PIPELINES = {
    "disease_spider.pipelines.DiseasePipeline": 300,
}

# 关闭警告
import warnings
warnings.filterwarnings("ignore", category=DeprecationWarning)