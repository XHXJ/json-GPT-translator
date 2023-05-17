// 最简代码，也就是这些字段必须有
export default {
  path: "/export",
  meta: {
    title: "导出文件",
    rank: 5
  },
  children: [
    {
      path: "/export/index",
      name: "export",
      component: () => import("@/views/export/index.vue"),
      meta: {
        title: "导出文件"
      }
    }
  ]
};
