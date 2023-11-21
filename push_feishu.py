import requests
import json
from datetime import datetime as dt
import argparse
import sys


def send_msg(_url, _msg):
    """
    :param _url:
    :param _msg:
    :return:
    """
    headers = {'Content-Type': 'application/json;charset=utf-8'}

    data = {
        "msg_type": "text",
        "content": {
            "text": "{0} {1}".format(dt.now().strftime('%Y-%m-%d %H:%M:%S'), _msg)
        }
    }

    r = requests.post(_url, data=json.dumps(data), headers=headers)
    return r.text


if __name__ == '__main__':
    # 注释掉的是不必要的调试用的
    # args_list = sys.argv
    # if args_list.__len__() < 2:
    #     print("加参数 --msg 后面跟要发送的消息")
    #     exit(1)
    #
    # arg1 = sys.argv[1]
    # if arg1 == '-h' or arg1 == '--help':
    #     print("加参数 --msg 后面跟要发送的消息")
    #     exit(0)
    parser = argparse.ArgumentParser(prog='feishu_alert',description='飞书消息通知消息')
    parser.add_argument('--msg', type=str, default=None, required=True, help="要发送的消息体")
    args = parser.parse_args()
    msg = args.msg
    url = 'https://open.feishu.cn/open-apis/bot/v2/hook/218184ab-df86-457b-87f1-d714ae342316'
    print(send_msg(url, msg))