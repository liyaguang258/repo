/**
 * 字典管理
 * @type {{}}
 */
const dict = {
    list({type}) {
        return red.postX('/dict/list', {type})
    }
}