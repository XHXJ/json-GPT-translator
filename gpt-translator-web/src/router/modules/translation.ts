export default {
  path: "/translation",
  meta: {
    title: "开始翻译",
    rank: 1
  },
  children: [
    {
      path: "/translation/index",
      name: "start",
      component: () => import("@/views/translation/start/index.vue"),
      meta: {
        title: "开始翻译",
        rank: 1
      }
    }
  ]
};
