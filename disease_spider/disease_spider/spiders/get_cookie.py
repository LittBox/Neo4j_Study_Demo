import requests
import time

def get_baidu_cookies():
    """获取百度cookies"""
    # 首先访问百度首页获取cookies
    baidu_url = "https://www.baidu.com"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
        'Accept-Language': 'zh-CN,zh;q=0.9',
        'Accept-Encoding': 'gzip, deflate',
        'Connection': 'keep-alive',
        'Upgrade-Insecure-Requests': '1',
    }
    
    session = requests.Session()
    
    try:
        # 1. 访问百度首页
        print("访问百度首页获取cookies...")
        response = session.get(baidu_url, headers=headers, timeout=10)
        print(f"百度首页状态码: {response.status_code}")
        print(f"获取到cookies: {session.cookies.get_dict()}")
        
        # 等待一下
        time.sleep(2)
        
        # 2. 使用获取的cookies访问百度百科
        baike_url = "https://baike.baidu.com/item/%E9%AB%98%E8%A1%80%E5%8E%8B/195863"
        
        # 更新headers
        headers.update({
            'Referer': 'https://www.baidu.com/',
            'Sec-Fetch-Dest': 'document',
            'Sec-Fetch-Mode': 'navigate',
            'Sec-Fetch-Site': 'same-site',
            'Sec-Fetch-User': '?1',
        })
        
        print("\n访问百度百科页面...")
        response = session.get(baike_url, headers=headers, timeout=10)
        
        print(f"百科页面状态码: {response.status_code}")
        print(f"响应大小: {len(response.text)} 字符")
        print(f"最终cookies: {session.cookies.get_dict()}")
        
        # 保存响应内容
        with open("baike_with_cookies.html", "w", encoding="utf-8") as f:
            f.write(response.text[:5000])
        
        # 检查是否成功
        if response.status_code == 200 and len(response.text) > 10000:
            print("✓ 成功获取百科页面")
            
            # 检查是否有基础信息
            if 'basic-info' in response.text:
                print("✓ 页面包含基础信息栏")
            if '就诊科室' in response.text:
                print("✓ 页面包含就诊科室信息")
            
            return session.cookies.get_dict()
        else:
            print("✗ 获取页面失败或页面内容过短")
            print(f"页面前200字符: {response.text[:200]}")
            return None
            
    except Exception as e:
        print(f"错误: {e}")
        return None

if __name__ == "__main__":
    cookies = get_baidu_cookies()
    if cookies:
        print(f"\n成功获取的cookies:")
        for key, value in cookies.items():
            print(f"  {key}: {value}")