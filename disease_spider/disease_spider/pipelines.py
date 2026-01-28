import json
import os

class DiseasePipeline:
    def open_spider(self, spider):
        """初始化"""
        self.save_path = "D:\\KnoledgeMap\\disease_spider\\disease_data.json"
        self.all_data = {}

    def process_item(self, item, spider):
        """处理每个疾病的爬取结果"""
        disease_name = item.get("中文名", "").strip()
        if disease_name and disease_name not in self.all_data:
            self.all_data[disease_name] = dict(item)
            spider.logger.info(f"已保存：{disease_name}")
        return item

    def close_spider(self, spider):
        """爬虫结束，写入JSON文件"""
        try:
            with open(self.save_path, "w", encoding="utf-8") as f:
                json.dump(self.all_data, f, ensure_ascii=False, indent=4)
            spider.logger.info(f"===== 爬取完成 =====\n共成功保存{len(self.all_data)}个疾病数据\n数据文件路径：{self.save_path}")
        except Exception as e:
            spider.logger.error(f"保存数据失败: {e}")