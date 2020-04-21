//import red from '../res/js/red'

const login = ({username,  pwd}) => {
    return red.postX("/user/login", {username, pwd, platToken: 'xx'})
}

const logout = () => {
    red.getJSON("/user/logout",{}, function () {
        red.showOk('退出成功')
        setTimeout(() => {
            location.href = "/user/login.html";
        }, 2000)
    })
}

export {login, logout}
