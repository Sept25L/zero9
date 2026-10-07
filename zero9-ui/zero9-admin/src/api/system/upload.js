import request from '@/utils/request'


export function upload(data) {
    return request({
        headers: { 'Content-Type': 'multipart/form-data' },
        url: '/upload/file',
        method: 'post',
        data
    })
}