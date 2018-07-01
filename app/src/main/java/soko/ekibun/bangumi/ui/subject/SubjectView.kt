package soko.ekibun.bangumi.ui.subject

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.entity.SectionEntity
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_subject.*
import kotlinx.android.synthetic.main.subject_blog.*
import kotlinx.android.synthetic.main.subject_detail.*
import kotlinx.android.synthetic.main.subject_episode.*
import kotlinx.android.synthetic.main.subject_topic.*
import soko.ekibun.bangumi.R
import soko.ekibun.bangumi.api.bangumi.bean.Episode
import soko.ekibun.bangumi.api.bangumi.bean.Subject
import soko.ekibun.bangumi.api.bangumi.bean.SubjectProgress
import soko.ekibun.bangumi.api.bangumi.bean.SubjectType
import soko.ekibun.bangumi.ui.main.fragment.calendar.CalendarAdapter
import soko.ekibun.bangumi.util.JsonUtil

class SubjectView(private val context: SubjectActivity){
    val episodeAdapter = SmallEpisodeAdapter()
    val episodeDetailAdapter = EpisodeAdapter()
    val linkedSubjectsAdapter = LinkedSubjectAdapter()
    val topicAdapter = TopicAdapter()
    val blogAdapter = BlogAdapter()
    val sitesAdapter = SitesAdapter()

    init{
        context.episode_list.adapter = episodeAdapter
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        context.episode_list.layoutManager = layoutManager
        context.episode_list.isNestedScrollingEnabled = false

        context.episode_detail_list.adapter = episodeDetailAdapter
        context.episode_detail_list.layoutManager = LinearLayoutManager(context)

        context.item_close.setOnClickListener {
            showEpisodeDetail(false)
        }
        context.episode_detail.setOnClickListener{
            showEpisodeDetail(true)
        }

        context.subject_list.adapter = linkedSubjectsAdapter
        val subjectLayoutManager = LinearLayoutManager(context)
        subjectLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        context.subject_list.layoutManager = subjectLayoutManager
        context.subject_list.isNestedScrollingEnabled = false

        context.topic_list.adapter = topicAdapter
        context.topic_list.layoutManager = LinearLayoutManager(context)
        context.topic_list.isNestedScrollingEnabled = false

        context.blog_list.adapter = blogAdapter
        context.blog_list.layoutManager = LinearLayoutManager(context)
        context.blog_list.isNestedScrollingEnabled = false

        context.site_list.adapter = sitesAdapter
        val flowLayoutManager = FlowLayoutManager()
        flowLayoutManager.isAutoMeasureEnabled = true
        context.site_list.layoutManager = flowLayoutManager
        context.site_list.isNestedScrollingEnabled = false
    }

    private fun parseSubject(subject: Subject): String{
        var ret = SubjectType.getDescription(subject.type) + "\n" +
                "总集数：${subject.eps_count}\n" +
                "开播时间：${subject.air_date}\n" +
                "更新时间："
        subject.air_weekday.toString().forEach {
            ret += CalendarAdapter.weekSmall[it.toString().toInt()] + " "
        }
        return ret
    }

    fun updateSubject(subject: Subject){
        if(context.isDestroyed) return
        //context.nested_scroll.tag = true
        //context.data_layout.visibility = View.VISIBLE
        context.title = if(subject.name_cn.isNullOrEmpty()) subject.name else subject.name_cn
        //item_title_header.text = title_text.text
        //context.item_summary.text = subject.summary
        context.item_info.text = parseSubject(subject)
        context.item_detail.text = subject.summary

        subject.rating?.let {
            context.item_score.text = it.score.toString()
            context.item_score_count.text = context.getString(R.string.rate_count, it.total)
        }
        Glide.with(context)
                .applyDefaultRequestOptions(RequestOptions.placeholderOf(context.item_cover.drawable))
                .load(subject.images?.common)
                .into(context.item_cover)

        Glide.with(context)
                .applyDefaultRequestOptions(RequestOptions.placeholderOf(context.item_cover_blur.drawable))
                .load(subject.images?.common)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 8)))
                .into(context.item_cover_blur)
        ((subject.eps as? List<*>)?.map{ JsonUtil.toEntity(JsonUtil.toJson(it!!), Episode::class.java)})?.let{
            updateEpisode(it)
        }
        topicAdapter.setNewData(subject.topic)
        blogAdapter.setNewData(subject.blog)
    }

    private fun updateEpisode(episodes: List<Episode>){
        val eps = episodes.filter { (it.status?:"") in listOf("Air") }.size
        context.episode_detail.text = context.getString(if(eps == episodes.size) R.string.phrase_full else R.string.phrase_updating, eps)

        val maps = HashMap<Int, List<Episode>>()
        episodes.forEach {
            maps[it.type] = (maps[it.type]?:ArrayList()).plus(it)
        }
        episodeAdapter.setNewData(null)
        episodeDetailAdapter.setNewData(null)
        maps.forEach {
            episodeDetailAdapter.addData(object: SectionEntity<Episode>(true, Episode.getTypeName(it.key)){})
            it.value.forEach {
                if((it.status?:"") in listOf("Air"))
                    episodeAdapter.addData(it)
                episodeDetailAdapter.addData(object: SectionEntity<Episode>(it){})
            }
        }
        progress = progress
    }

    private var scrolled = false
    var loaded_progress = false
    var progress: SubjectProgress? = null
        set(value) {
            episodeDetailAdapter.data.forEach { ep ->
                ep.t?.progress = null
                value?.eps?.forEach {
                    if (ep.t?.id == it.id) {
                        ep.t?.progress = it
                    }
                }
            }
            episodeAdapter.notifyDataSetChanged()
            episodeDetailAdapter.notifyDataSetChanged()
            field = value

            if(!scrolled && loaded_progress && episodeAdapter.data.size>0){
                scrolled = true

                var lastView = 0
                episodeAdapter.data.forEachIndexed { index, episode ->
                    if(episode.progress != null)
                        lastView = index
                }
                val layoutManager = (context.episode_list.layoutManager as LinearLayoutManager)
                layoutManager.scrollToPositionWithOffset(lastView, 0)
                layoutManager.stackFromEnd = false
            }
        }

    fun showEpisodeDetail(show: Boolean){
        context.episode_detail_list_header.visibility = if(show) View.VISIBLE else View.INVISIBLE
        context.episode_detail_list_header.animation = AnimationUtils.loadAnimation(context, if(show) R.anim.move_in else R.anim.move_out)
        context.episode_detail_list.visibility = if(show) View.VISIBLE else View.INVISIBLE
        context.episode_detail_list.animation = AnimationUtils.loadAnimation(context, if(show) R.anim.move_in else R.anim.move_out)
    }
}