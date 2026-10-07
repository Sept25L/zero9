import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import path from 'path'

export default function setupSvgIcons() {
  return createSvgIconsPlugin({
    // 指定图标文件夹路径
    iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
    // 设置symbol ID格式
    symbolId: 'icon-[name]',
    // （可选）自定义插入位置，如：'body-first' 或 'body-last'
    // inject: 'body-last',
    // （可选）自定义DOM ID
    // customDomId: '__svg__icons__dom__'
  })
}