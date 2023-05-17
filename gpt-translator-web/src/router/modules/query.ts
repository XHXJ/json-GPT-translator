// 最简代码，也就是这些字段必须有
export default {
  path: "/query",
  meta: {
    title: "数据管理",
    rank: 4
  },
  children: [
    {
      path: "/query/project",
      name: "project",
      component: () => import("@/views/query/project/index.vue"),
      meta: {
        title: "项目查询"
      }
    },
    {
      path: "/query/translate",
      name: "translate",
      component: () => import("@/views/query/translate/index.vue"),
      meta: {
        title: "翻译查询"
      }
    }
  ]
};
