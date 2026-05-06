package com.ElOuedUniv.maktaba.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            // تأكد من أن هذا هو رابط المشروع الصحيح
            supabaseUrl = "https://txvhiftgekdphwmgwyqe.supabase.co",
            // ملاحظة: هذا المفتاح يبدو غير صحيح. يجب استخدام 'anon key' من إعدادات API في Supabase
            // وعادة ما يبدأ بـ 'eyJhbGciOi...'
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR4dmhpZnRnZWtkcGh3bWd3eXFlIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3NzUzNDA1MywiZXhwIjoyMDkzMTEwMDUzfQ.k1tIt-yNf_l6idU0K-bW7HkPePF2QYyRToqDP5lPceI"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }
}
