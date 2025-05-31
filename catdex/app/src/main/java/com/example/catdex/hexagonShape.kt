import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Un Shape Compose qui dessine un hexagone régulier occupant tout l’espace de la boîte.
 */
fun hexagonShape(): GenericShape = GenericShape { size, _ ->
    // Taille du conteneur
    val w = size.width
    val h = size.height

    // Calcul des points pour un hexagone régulier centré
    // On part du point en haut-centre, puis on trace dans le sens horaire.
    val halfWidth = w / 2f
    val quarterWidth = w / 4f
    val triHeight = (h / 2f)

    // Les six sommets (en coordonnées relatives) :
    //   1) (w/2, 0)
    //   2) (w, h/4)
    //   3) (w, 3h/4)
    //   4) (w/2, h)
    //   5) (0, 3h/4)
    //   6) (0, h/4)
    moveTo(halfWidth, 0f)
    lineTo(w, h / 4f)
    lineTo(w, 3f * h / 4f)
    lineTo(halfWidth, h)
    lineTo(0f, 3f * h / 4f)
    lineTo(0f, h / 4f)
    close()
}
