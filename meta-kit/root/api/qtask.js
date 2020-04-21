const qtask = {
    qtaskList() {
        return red.postX('_qtask/list', {})
    },
    qtaskSave({task}) {
        return red.postX('_qtask/save', {task: JSON.stringify(task)})
    },
    qtaskDebug({task}) {
        return red.postX('_qtask/debug', {task: JSON.stringify(task)})
    }
}