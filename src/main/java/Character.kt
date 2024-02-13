import org.json.JSONObject
import java.util.*

class Character {
    private var id = ""
    private val fields = HashMap<String, String>()
    private var avatar: String = ""

    private val titles = ArrayList<String>()

    fun addDataField(title: String, data: String) {
        titles.add(title)
        fields[title] = data
    }

    fun setID(id: String) { this.id = id }
    fun getID(): String { return id }

    fun getDataField(title: String) : String? {
        return fields[title]
    }

    fun getTitles(): ArrayList<String> { return titles }

    fun setAvatar(avatar: String) { this.avatar = avatar }
    fun getAvatar(): String { return avatar }

    override fun toString(): String {
        val json = JSONObject()

        val array = ArrayList<String?>()
        for (i in 0..<titles.size) {
            array.add(titles[i])
            array.add(fields[titles[i]])
        }
        json.put("data", array.toString())



        json.put("avatar", avatar)

        return json.toString();
    }
}