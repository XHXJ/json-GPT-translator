// 最简代码，也就是这些字段必须有
export default {
  path: "/configure",
  meta: {
    title: "配置管理"
  },
  children: [
    {
      path: "/configure/index",
      name: "key",
      component: () => import("@/views/configure/key/index.vue"),
      meta: {
        title: "openai key管理"
      }
    },
    {
      path: "/fighting/effort",
      name: "prompt",
      component: () => import("@/views/configure/prompt/index.vue"),
      meta: {
        title: "prompt配置"
      }
    }
  ]
};
