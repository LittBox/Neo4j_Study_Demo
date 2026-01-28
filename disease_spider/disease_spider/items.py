import scrapy

# 这是标准的DiseaseItem定义，没有任何语法错误
class DiseaseItem(scrapy.Item):
    中文名 = scrapy.Field()
    就诊科室 = scrapy.Field()
    常见病因 = scrapy.Field()
    常见症状 = scrapy.Field()
    相关药物 = scrapy.Field()