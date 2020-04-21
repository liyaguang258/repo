var red = {
    showMsg: function(option) {
        var defOption = {msg: "操作成功", type:"info", placement: "top"};

        option = option || defOption;
        for (var k in defOption) {
            option[k] = option[k] || defOption[k]
        }

        new $.zui.Messager(option.msg, {
            type: option.type // 定义颜色主题
            ,placement: option.placement
        }).show();
    },
    showOk(msg = '操作成功') {
        red.showMsg({msg})
    },
    showError(msg = '操作失败 ') {
        red.showMsg({type:"error", msg})
    },
    getData: function(key, defaultValue) {
        var v = localStorage.getItem(key) || defaultValue || "";
        if (typeof(v) == "string" && v.startsWith("{") && v.endsWith("}")) {
            v = JSON.parse(v);
        } else if (typeof(v) == "string" && v.startsWith("[") && v.endsWith("]")) {
            v = JSON.parse(v);
        }
        return v;
    },
    setData: function(key, value) {
        var v = value;
        if (typeof(v) == "object") {
            v = JSON.stringify(value);
        }
        localStorage.setItem(key, v);
    },
    getPlatId: function() {
        var plat = red.getData("sysPlat") || {};
        return plat["_key"];
    },
    getPlatToken: function() {
        let plat = red.getData("sysPlat");
        if (!plat) {
            red.showMsg({type:'error', placement: 'center', msg: '登陆过期，请前往登陆'});
            setTimeout(function () {
                location.href = "/user/login.html";
            }, 2000);
        }
        return plat["token"];
    },
    getJSON: function (url, params = {}, callback) {
        params["platToken"] = red.getPlatToken()
        axios.get(url, {params}).then(res => {
            let data = res.data || {}
            red.loginCheck(data)
            if (data.code == -1) {
                red.showMsg({type:"error", msg: data.message})
                return;
            }
            if (data.code == 0) {
                data = data.body
            }
            callback(data)
        })
    },
    getX(url, params = {}) {
        if (!params['platToken'])
            params['platToken'] = red.getPlatToken()
        return new Promise(resolve => {
            axios.get(url, params).then(res => {
                let data = res.data || {}
                red.loginCheck(data)
                if (data.code == -1) {
                    red.showMsg({type:"error", msg: data.message})
                    return;
                }
                if (data.code == 0) {
                    data = data.body
                }

                resolve(data)
            }).catch(res => {
                console.log(res)
                red.showMsg({type:"error", msg:'操作失败!'})
            })
        })
    },
    postX(url, params = {}) {
        if (!params['platToken'])
            params['platToken'] = red.getPlatToken()
        return new Promise(resolve => {
            axios({
                url,
                method: 'post',
                data: params,
                transformRequest: [data => {
                    let _data = ''
                    for (k in data) {
                        if (data[k] != undefined) {
                            _data += k + '=' + data[k] + '&'
                        }
                    }
                    return _data
                }
                ]
            }).then(res => {
                let data = res.data || {}
                red.loginCheck(data)
                if (data.code == -1) {
                    red.showMsg({type:"error", msg: data.message})
                    return;
                }
                else if (data.code == 0) {
                    data = data.body
                }
                resolve(data)
            });
        })
    },
    post: function(url, params = {}, callback) {
        params['platToken'] = red.getPlatToken()
        axios.post(url, params).then(res => {
            let data = red.loginCheck(res.data)
            if (data && data.code == -1) {
                red.showMsg({msg: data.message, type: "error"})
                return;
            }

            if (callback) {
                callback(data.code == 0 ? data.body : data)
            } else {
                red.showMsg()
            }
        })
    },

    //TODO: 提取统一查询、请求，失败提示
    //QTASK find list
    qtaskCall: function (para, callback) {

        /*$.p$.post("/db/list", {fBean: JSON.stringify(fBean)}, function (json) {
            vm.list = json.body;
        });*/
        $.getJSON("/qtask/call", para, function (json) {
            callback(json);
        });
    },

    //db find
    dbQuery: function (para, callback) {
        $.getJSON("/db/list", para, function (json) {
            red.loginCheck(json);
            if (json.code == -1) {
                console.log("json.code == -1")
            }

            callback(json);
        });
    },

    putAll: function(t, s) {
        t = t || {};
        s = s || {};

        for (var k in s) {
            t[k] = s[k];
        }
        return t;
    },

    timeFmt: function (date,fmt){
        fmt = fmt || "yyyy-MM-dd HH:mm:ss";
        var o = {
            "M+" : date.getMonth()+1,                 //月份
            "d+" : date.getDate(),                    //日
            "H+" : date.getHours(),                   //小时
            "m+" : date.getMinutes(),                 //分
            "s+" : date.getSeconds(),                 //秒
            "q+" : Math.floor((date.getMonth()+3)/3), //季度
            "S"  : date.getMilliseconds()             //毫秒
        };

        if(/(y+)/.test(fmt))
            fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));
        for(var k in o)
            if(new RegExp("("+ k +")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        return fmt;
    },
    loginCheck: function (json) {
        if (json && (json['code'] == -2 || json['referid'])) {
            red.showMsg({type:'error', placement: 'center', msg: '登陆过期，请前往登陆'});
            setTimeout(function () {
                location.href = "/user/login.html";
            }, 2000);
        }
    },
    replaceAll: function (d, s, t) {
        let reg=new RegExp(s,"g"); //创建正则RegExp对象
        let str = JSON.stringify(d);
        str = str.replace(reg, t)
        return JSON.parse(str);
    },
    replacePoint: function(d) {
        return red.replaceAll(d, "[.]", "$");
    },
    replace$: function (d) {
        return red.replaceAll(d, "[$]", ".");
    }
}

String.prototype.replaceAll=function(s,t){
    return red.replaceAll(this, s, t);
}