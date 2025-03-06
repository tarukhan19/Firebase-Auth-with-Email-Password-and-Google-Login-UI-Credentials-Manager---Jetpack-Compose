package com.demo.userauth.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.userauth.presentation.theme.primaryColor

@Composable
fun CustomButton(
    isButtonEnabled: Boolean = false,
    containerColor: Color = primaryColor,
    contentColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(percent = 20),
    elevation: ButtonElevation = ButtonDefaults.buttonElevation(
        defaultElevation = 8.dp,
        pressedElevation = 8.dp,
        disabledElevation = 0.dp
    ),
    onClick: () -> Unit,
    icon: ImageVector? = null,
    buttonContent: String = ""
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .size(50.dp),
        enabled = isButtonEnabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = elevation,
    )
    {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(
            text = buttonContent,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

    }
}
