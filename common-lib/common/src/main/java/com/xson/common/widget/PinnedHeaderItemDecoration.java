package com.xson.common.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * jecelyin: 移除header click listener
 * Created by Oubowu on 2016/7/21 15:38.
 * <p>
 * 这个是单独一个布局的标签
 * <p>
 * porting from https://github.com/takahr/pinned-section-item-decoration
 * <p>
 * 注意：标签所在最外层布局不能设置marginTop，因为往上滚动遮不住真正的标签;marginBottom还有问题待解决
 */
public class PinnedHeaderItemDecoration<T> extends RecyclerView.ItemDecoration {

    /**
     * Recycler的Adapter必须继承此接口来告诉ItemDecoration粘性标签的类型和某个位置粘性标签的信息
     */
    public interface PinnedHeaderNotifyer<T> {

        boolean isPinnedHeaderType(int viewType);

        T getPinnedHeaderInfo(int position);

    }

    private boolean mEnableDivider;

    private boolean mDisableHeaderClick;

    private int mDividerId;

    private int[] mClickIds;

    private Drawable mDrawable;
    private RecyclerView.Adapter mAdapter;

    // 缓存的标签
    private View mPinnedHeaderView;

    // 缓存的标签位置
    int mPinnedHeaderPosition = -1;

    // 顶部标签的Y轴偏移值
    private int mPinnedHeaderOffset;

    // 用于锁定画布绘制范围
    private Rect mClipBounds;

    // 父布局的左间距
    private int mRecyclerViewPaddingLeft;
    // 父布局的顶间距
    private int mRecyclerViewPaddingTop;

    private int mHeaderLeftMargin;
    private int mHeaderTopMargin;
    private int mHeaderRightMargin;
    private int mHeaderBottomMargin;

    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;

    // 当我们调用mRecyclerView.addItemDecoration()方法添加decoration的时候，RecyclerView在绘制的时候，去会绘制decorator，即调用该类的onDraw和onDrawOver方法，
    // 1.onDraw方法先于drawChildren
    // 2.onDrawOver在drawChildren之后，一般我们选择复写其中一个即可。
    // 3.getItemOffsets 可以通过outRect.set()为每个Item设置一定的偏移量，主要用于绘制Decorator。

    private PinnedHeaderItemDecoration(Builder<T> builder) {
        mEnableDivider = builder.enableDivider;
        mDividerId = builder.dividerId;
        mClickIds = builder.clickIds;
        mDisableHeaderClick = builder.disableHeaderClick;
    }

    @Override
    public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent, RecyclerView.State state) {

        checkCache(parent);

        if (!mEnableDivider) {
            return;
        }

        if (mDrawable == null && mDividerId != 0) {
            mDrawable = ContextCompat.getDrawable(parent.getContext(), mDividerId);
        }

        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            if (!isPinnedHeader(parent, view)) {
                final int spanCount = getSpanCount(parent);
                int position = parent.getChildAdapterPosition(view);
                if (isFirstColumn(parent, position, spanCount)) {
                    // 第一列要多画左边
                    outRect.set(mDrawable.getIntrinsicWidth(), 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
                } else {
                    outRect.set(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
                }
            } else {
                // 标签画底部分隔线
                outRect.set(0, 0, 0, mDrawable.getIntrinsicHeight());
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            outRect.set(0, 0, 0, mDrawable.getIntrinsicHeight());
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            if (isPinnedHeader(parent, view)) {
                outRect.set(0, 0, 0, mDrawable.getIntrinsicHeight());
            } else {
                final StaggeredGridLayoutManager.LayoutParams slp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                // slp.getSpanIndex(): 这个可以拿到它在同一行排序的真实顺序
                if (slp.getSpanIndex() == 0) {
                    outRect.set(mDrawable.getIntrinsicWidth(), 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
                } else {
                    outRect.set(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
                }
            }
        }
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        // 检测到标签存在的时候，将标签强制固定在顶部
        createPinnedHeader(parent);

        if (mPinnedHeaderView != null) {

            mClipBounds = c.getClipBounds();
            // getTop拿到的是它的原点(它自身的padding值包含在内)相对parent的顶部距离，加上它的高度后就是它的底部所处的位置
            final int headEnd = mPinnedHeaderView.getTop() + mPinnedHeaderView.getHeight();
            // 根据坐标查找view，headEnd + 1找到的就是mPinnedHeaderView底部下面的view
            final View belowView = parent.findChildViewUnder(c.getWidth() / 2, headEnd + 1);
            if (isPinnedHeader(parent, belowView)) {
                // 如果是标签的话，缓存的标签就要同步跟此标签移动
                // 根据belowView相对顶部距离计算出缓存标签的位移
                mPinnedHeaderOffset = belowView.getTop() - (mRecyclerViewPaddingTop + mPinnedHeaderView.getHeight() + mHeaderTopMargin);
                // 锁定的矩形顶部为v.getTop(趋势是mPinnedHeaderView.getHeight()->0)
                mClipBounds.top = belowView.getTop();
            } else {
                mPinnedHeaderOffset = 0;
                mClipBounds.top = mRecyclerViewPaddingTop + mPinnedHeaderView.getHeight();
            }
            // 锁定画布绘制范围，记为A
            c.clipRect(mClipBounds);
        }

        if (mEnableDivider) {
            drawDivider(c, parent);
        }

    }

    // 画分隔线
    private void drawDivider(Canvas c, RecyclerView parent) {

        if (mAdapter == null) {
            // checkCache的话RecyclerView未设置之前mAdapter为空
            return;
        }

        // 不让分隔线画出界限
        c.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getWidth() - parent.getPaddingRight(), parent.getHeight() - parent.getPaddingBottom());

        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            int childCount = parent.getChildCount();
            final int spanCount = getSpanCount(parent);
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                // 要考虑View的重用啊
                int realPosition = parent.getChildAdapterPosition(child);
                if (isPinnedHeaderType(mAdapter.getItemViewType(realPosition))) {
                    DividerHelper.drawBottomAlignItem(c, mDrawable, child, params);
                } else {
                    if (isFirstColumn(parent, realPosition, spanCount)) {
                        DividerHelper.drawLeft(c, mDrawable, child, params);
                    }
                    DividerHelper.drawBottom(c, mDrawable, child, params);
                    DividerHelper.drawRight(c, mDrawable, child, params);
                }
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                DividerHelper.drawBottomAlignItem(c, mDrawable, child, params);
            }
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                if (isPinnedHeader(parent, child)) {
                    DividerHelper.drawBottomAlignItem(c, mDrawable, child, params);
                } else {
                    DividerHelper.drawLeft(c, mDrawable, child, params);
                    DividerHelper.drawBottom(c, mDrawable, child, params);
                    DividerHelper.drawRight(c, mDrawable, child, params);
                }
            }
        }

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        if (mPinnedHeaderView != null) {
            c.save();

            mClipBounds.top = mRecyclerViewPaddingTop + mHeaderTopMargin;
            // 锁定画布绘制范围，记为B
            // REVERSE_DIFFERENCE，实际上就是求得的B和A的差集范围，即B－A，只有在此范围内的绘制内容才会被显示
            // 因此,只绘制(0,0,parent.getWidth(),belowView.getTop())这个范围，然后画布移动了mPinnedHeaderTop，所以刚好是绘制顶部标签移动的范围
            // 低版本不行，换回Region.Op.UNION并集
            c.clipRect(mClipBounds, Region.Op.UNION);
            c.translate(mRecyclerViewPaddingLeft + mHeaderLeftMargin, mPinnedHeaderOffset + mRecyclerViewPaddingTop + mHeaderTopMargin);
            mPinnedHeaderView.draw(c);

            c.restore();
        }
    }

    /**
     * 查找到view对应的位置从而判断出是否标签类型
     *
     * @param parent
     * @param view
     * @return
     */
    private boolean isPinnedHeader(RecyclerView parent, View view) {
        final int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            return false;
        }
        final int type = mAdapter.getItemViewType(position);
        return isPinnedHeaderType(type);
    }

    /**
     * 创建标签强制固定在顶部
     *
     * @param parent
     */
    @SuppressWarnings("unchecked")
    private void createPinnedHeader(RecyclerView parent) {

        if (mAdapter == null) {
            // checkCache的话RecyclerView未设置之前mAdapter为空
            return;
        }

        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        // 获取第一个可见的item位置
        int firstVisiblePosition = findFirstVisiblePosition(layoutManager);

        // 获取标签的位置，
        int pinnedHeaderPosition = findPinnedHeaderPosition(firstVisiblePosition);
        if (pinnedHeaderPosition >= 0 && mPinnedHeaderPosition != pinnedHeaderPosition) {

            // 标签位置有效并且和缓存的位置不同
            mPinnedHeaderPosition = pinnedHeaderPosition;
            // 获取标签的type
            final int type = mAdapter.getItemViewType(mPinnedHeaderPosition);

            // 手动调用创建标签
            final RecyclerView.ViewHolder holder = mAdapter.createViewHolder(parent, type);
            mAdapter.bindViewHolder(holder, mPinnedHeaderPosition);
            // 缓存标签
            mPinnedHeaderView = holder.itemView;

            ViewGroup.LayoutParams lp = mPinnedHeaderView.getLayoutParams();
            if (lp == null) {
                // 标签默认宽度占满parent
                lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mPinnedHeaderView.setLayoutParams(lp);
            }

            // 对高度进行处理
            int heightMode = View.MeasureSpec.getMode(lp.height);
            int heightSize = View.MeasureSpec.getSize(lp.height);

            if (heightMode == View.MeasureSpec.UNSPECIFIED) {
                heightMode = View.MeasureSpec.EXACTLY;
            }

            mRecyclerViewPaddingLeft = parent.getPaddingLeft();
            int recyclerViewPaddingRight = parent.getPaddingRight();
            mRecyclerViewPaddingTop = parent.getPaddingTop();
            int recyclerViewPaddingBottom = parent.getPaddingBottom();

            if (lp instanceof ViewGroup.MarginLayoutParams) {
                final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mHeaderLeftMargin = mlp.leftMargin;
                mHeaderTopMargin = mlp.topMargin;
                mHeaderRightMargin = mlp.rightMargin;
                mHeaderBottomMargin = mlp.bottomMargin;
            }

            // 最大高度为RecyclerView的高度减去padding
            final int maxHeight = parent.getHeight() - mRecyclerViewPaddingTop - recyclerViewPaddingBottom;
            // 不能超过maxHeight
            heightSize = Math.min(heightSize, maxHeight);

            // 因为标签默认宽度占满parent，所以宽度强制为RecyclerView的宽度减去padding
            final int widthSpec = View.MeasureSpec
                    .makeMeasureSpec(parent.getWidth() - mRecyclerViewPaddingLeft - recyclerViewPaddingRight - mHeaderLeftMargin - mHeaderRightMargin,
                            View.MeasureSpec.EXACTLY);
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(heightSize, heightMode);
            // 强制测量
            mPinnedHeaderView.measure(widthSpec, heightSpec);

            mLeft = mRecyclerViewPaddingLeft + mHeaderLeftMargin;
            mTop = mRecyclerViewPaddingTop + mHeaderTopMargin;
            mRight = mPinnedHeaderView.getMeasuredWidth() + mRecyclerViewPaddingLeft + mHeaderLeftMargin + mHeaderRightMargin;
            mBottom = mPinnedHeaderView.getMeasuredHeight() + mRecyclerViewPaddingTop + mHeaderTopMargin + mHeaderBottomMargin;

            // 位置强制布局在顶部
            mPinnedHeaderView.layout(mLeft, mTop, mRight - mHeaderRightMargin, mBottom - mHeaderBottomMargin);
        }

    }

    /**
     * 从传入位置递减找出标签的位置
     *
     * @param formPosition
     * @return
     */
    private int findPinnedHeaderPosition(int formPosition) {

        for (int position = formPosition; position >= 0; position--) {
            // 位置递减，只要查到位置是标签，立即返回此位置
            final int type = mAdapter.getItemViewType(position);
            if (isPinnedHeaderType(type)) {
                return position;
            }
        }

        return 0;
    }

    /**
     * 通过适配器告知类型是否为标签
     *
     * @param type
     * @return
     */
    private boolean isPinnedHeaderType(int type) {
        return ((PinnedHeaderNotifyer) mAdapter).isPinnedHeaderType(type);
    }

    /**
     * 找出第一个可见的Item的位置
     *
     * @param layoutManager
     * @return
     */
    private int findFirstVisiblePosition(RecyclerView.LayoutManager layoutManager) {
        int firstVisiblePosition = 0;
        if (layoutManager instanceof GridLayoutManager) {
            firstVisiblePosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            firstVisiblePosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(into);
            firstVisiblePosition = Integer.MAX_VALUE;
            for (int pos : into) {
                firstVisiblePosition = Math.min(pos, firstVisiblePosition);
            }
        }
        return firstVisiblePosition;
    }

    /**
     * 检查缓存
     *
     * @param parent
     */
    private void checkCache(final RecyclerView parent) {
        final RecyclerView.Adapter adapter = parent.getAdapter();
        if (mAdapter != adapter) {
            // 适配器为null或者不同，清空缓存
            mPinnedHeaderView = null;
            mPinnedHeaderPosition = -1;
            // 明确了适配器必须继承PinnedHeaderNotifyer接口，因为没有这个就获取不到哪个位置对应的类型是标签类型
            if (adapter instanceof PinnedHeaderNotifyer) {
                mAdapter = adapter;
            } else {
                throw new IllegalStateException("Adapter must implements " + PinnedHeaderNotifyer.class.getSimpleName());
            }
        }
    }

    /**
     * 适用于网格布局，用于判断是否是第一列
     *
     * @param parent
     * @param pos
     * @param spanCount
     * @return
     */
    private boolean isFirstColumn(RecyclerView parent, int pos, int spanCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final int headerPosition = findPinnedHeaderPosition(pos);
            if ((pos - (headerPosition + 1)) % spanCount == 0) {
                // 找到头部位置减去包括头部位置之前的个数
                return true;
            }
        }
        return false;
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    public static class Builder<T> {

        private int dividerId;

        private boolean enableDivider;

        private int[] clickIds;

        public boolean disableHeaderClick;

        public Builder() {
        }

        /**
         * 设置分隔线资源ID
         *
         * @param dividerId 资源ID，若不设置这个并且enableDivider=true时，使用默认的分隔线
         * @return 构建者
         */
        public Builder<T> setDividerId(int dividerId) {
            this.dividerId = dividerId;
            return this;
        }

        /**
         * 是否开启绘制分隔线，默认关闭
         *
         * @param enableDivider true为绘制，false不绘制，false时setDividerId无效
         * @return 构建者
         */
        public Builder<T> enableDivider(boolean enableDivider) {
            this.enableDivider = enableDivider;
            return this;
        }

        /**
         * 通过传入包括标签和其内部的子控件的ID设置其对应的点击事件
         *
         * @param clickIds 标签或其内部的子控件的ID
         * @return 构建者
         */
        public Builder<T> setClickIds(int... clickIds) {
            this.clickIds = clickIds;
            return this;
        }

        /**
         * 开启或关闭标签点击事件(不包括标签里面的子控件)，默认开启，当setHeaderClickListener不为null时有效
         *
         * @param disableHeaderClick true为关闭标签点击事件，false为开启标签点击事件
         * @return 构建者
         */
        public Builder<T> disableHeaderClick(boolean disableHeaderClick) {
            this.disableHeaderClick = disableHeaderClick;
            return this;
        }

        public PinnedHeaderItemDecoration<T> create() {
            return new PinnedHeaderItemDecoration<T>(this);
        }

    }

    private static class DividerHelper {

        // 将分割线画在view的顶部，并且左右会多出分割线的宽度
        public static void drawTop(Canvas canvas, Drawable drawable, View child, ViewGroup.MarginLayoutParams params) {

            final int left = child.getLeft() - params.leftMargin - drawable.getIntrinsicWidth();
            final int right = child.getRight() + params.rightMargin + drawable.getIntrinsicWidth();
            final int top = child.getTop() - params.topMargin - drawable.getIntrinsicHeight();
            final int bottom = top + drawable.getIntrinsicHeight();

            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }

        // 将分割线画在view的底部，并且左右会多出分割线的宽度
        public static void drawBottom(Canvas canvas, Drawable drawable, View child, ViewGroup.MarginLayoutParams params) {

            final int left = child.getLeft() - params.leftMargin - drawable.getIntrinsicWidth();
            final int right = child.getRight() + params.rightMargin + drawable.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + drawable.getIntrinsicHeight();

            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }

        // 将分割线画在view的左边，并且上下会多出分割线的高度
        public static void drawLeft(Canvas canvas, Drawable drawable, View child, ViewGroup.MarginLayoutParams params) {

            final int top = child.getTop() - params.topMargin - drawable.getIntrinsicHeight();
            final int bottom = child.getBottom() + params.bottomMargin + drawable.getIntrinsicHeight();
            final int left = child.getLeft() - params.leftMargin - drawable.getIntrinsicWidth();
            final int right = left + drawable.getIntrinsicWidth();

            drawable.setBounds(left, top, right, bottom);

            drawable.draw(canvas);
        }

        // 将分割线画在view的右边，并且上下会多出分割线的高度
        public static void drawRight(Canvas canvas, Drawable drawable, View child, ViewGroup.MarginLayoutParams params) {

            final int top = child.getTop() - params.topMargin - drawable.getIntrinsicHeight();
            final int bottom = child.getBottom() + params.bottomMargin + drawable.getIntrinsicHeight();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + drawable.getIntrinsicWidth();

            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }


        // 将分割线画在view的顶部，与view左右对齐，考虑margin值
        public static void drawTopAlignItem(Canvas canvas, Drawable drawable, View child, ViewGroup.MarginLayoutParams params) {

            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getTop() - params.topMargin - drawable.getIntrinsicHeight();
            final int bottom = top + drawable.getIntrinsicHeight();

            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }

        // 将分割线画在view的底部，与view左右对齐，考虑margin值
        public static void drawBottomAlignItem(Canvas canvas, Drawable drawable, View child, ViewGroup.MarginLayoutParams params) {

            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + drawable.getIntrinsicHeight();

            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }

        // 将分割线画在view的左边，与Item上下对齐，考虑margin值
        public static void drawLeftAlignItem(Canvas canvas, Drawable drawable, View child, ViewGroup.MarginLayoutParams params) {

            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getLeft() - params.leftMargin - drawable.getIntrinsicWidth();
            final int right = left + drawable.getIntrinsicWidth();

            drawable.setBounds(left, top, right, bottom);

            drawable.draw(canvas);
        }

        // 将分割线画在view的右边，与Item上下对齐，考虑margin值
        public static void drawRightAlignItem(Canvas canvas, Drawable drawable, View child, ViewGroup.MarginLayoutParams params) {

            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + drawable.getIntrinsicWidth();

            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }

    }

    public static class FullSpanUtil {

        public static void onAttachedToRecyclerView(RecyclerView recyclerView, final RecyclerView.Adapter adapter, final int pinnedHeaderType) {
            // 如果是网格布局，这里处理标签的布局占满一行
            final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                final GridLayoutManager.SpanSizeLookup oldSizeLookup = gridLayoutManager.getSpanSizeLookup();
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (adapter.getItemViewType(position) == pinnedHeaderType) {
                            return gridLayoutManager.getSpanCount();
                        }
                        if (oldSizeLookup != null) {
                            return oldSizeLookup.getSpanSize(position);
                        }
                        return 1;
                    }
                });
            }
        }

        public static void onViewAttachedToWindow(RecyclerView.ViewHolder holder, RecyclerView.Adapter adapter, int pinnedHeaderType) {
            // 如果是瀑布流布局，这里处理标签的布局占满一行
            final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                final StaggeredGridLayoutManager.LayoutParams slp = (StaggeredGridLayoutManager.LayoutParams) lp;
                slp.setFullSpan(adapter.getItemViewType(holder.getLayoutPosition()) == pinnedHeaderType);
            }
        }


    }
}