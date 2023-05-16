// 最简代码，也就是这些字段必须有
export default {
  path: "/upload",
  meta: {
    title: "上传文件"
  },
  children: [
    {
      path: "/upload/index",
      name: "upload",
      component: () => import("@/views/upload/index.vue"),
      meta: {
        title: "上传文件"
      }
    }
  ]
};
