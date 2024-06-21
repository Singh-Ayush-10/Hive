import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mastercodint.authentaction.PostDetails
import com.mastercodint.authentaction.R
import com.mastercodint.authentaction.postinfo

class ImageAdapter(private val context: Context, private val images: MutableList<postinfo>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {


    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent,false))
        return view
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val postItem = images[position]
        val imageUrl = postItem.imageUrl
        val postId = postItem.postId
        Glide.with(holder.imageView)
            .load(imageUrl)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context,PostDetails::class.java)
            intent.putExtra("postId", postId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}


