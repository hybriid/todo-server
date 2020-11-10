package todoServer
import io.getquill._
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import com.opentable.db.postgres.embedded.EmbeddedPostgres

object main extends cask.MainRoutes {
  case class Note(id: String, content: String)
  val server = EmbeddedPostgres.builder()
    .setDataDirectory(System.getProperty("user.home") + "/data")
    .setCleanDataDirectory(false).setPort(5432)
    .start()
  val pgDataSource = new org.postgresql.ds.PGSimpleDataSource()
  pgDataSource.setUser("postgres")
  val hikariConfig = new HikariConfig()
  hikariConfig.setDataSource(pgDataSource)
  val ctx = new PostgresJdbcContext(LowerCase, new HikariDataSource(hikariConfig))
  ctx.executeAction("CREATE TABLE IF NOT EXISTS note (id text, content text);")
  import ctx._

  def addNoteDatabase(id: String, content: String) = {
    ctx.run(query[Note].insert(lift(Note(id, content))))
  }

  def deleteNoteDatabase(id: String) = {
    ctx.run(query[Note].filter(note => note.id == lift(id)).delete)
  }

  def updateNoteDatabase(id: String, content: String) {
    ctx.run(query[Note].filter(note => note.id == lift(id)).update(Note(lift(id), lift(content))))
  }

  def getAllNotes() = {
    ctx.run(query[Note].map(n => (n.id, n.content)))
  }

  @cask.postJson("/add")
  def addNote(id: String, content: String) = {
    addNoteDatabase(id, content)
    getAllNotes()
  }

  @cask.postJson("/delete")
  def deleteNote(id: String) = {
    deleteNoteDatabase(id)
    getAllNotes()
  }

  @cask.postJson("/update")
  def deleteNote(id: String, content: String) = {
    updateNoteDatabase(id, content)
    getAllNotes()
  }

  @cask.post("/do-thing")
  def doThing(request: cask.Request) = {
    request.text().reverse
  }

  initialize()
}