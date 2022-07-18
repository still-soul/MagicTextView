# MagicTextView

满足一些场景下的自定义TextView，持续更新。

1、MoreTextView :使用viewTreeObserver.addOnPreDrawListener监听实现 超过最大行数显示...全文

2、ListMoreTextView :重写onMeasure（）方法 实现 超过最大行数显示...全文，支持链接的显示及交互

3、LinkTextView ：链接的显示和交互

4、圆角 可以设置背景色、指定圆角、描边的宽度和颜色
   4.1）RoundButton ：圆角TextView
   4.2）RoundFrameLayout : 圆角帧布局
   4.3）RoundLinearLayout：圆角线性布局
   4.4）RoundConstraintLayout：圆角约束布局

5、agSpanText ：TextView tag 标签

6、LikeView ：点赞按钮，配合LikeAnimationLayout使用实现点赞动画，具体用法请看ListActivity