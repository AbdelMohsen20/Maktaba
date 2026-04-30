# Supabase Database Schema for Maktaba App

يمكنك نسخ هذا الكود إلى محرر SQL في Supabase لإنشاء الجداول اللازمة لتطبيق المكتبة.


```sql
-- جدول التصنيفات
create table if not exists categories (
  id text primary key,
  name text not null,
  description text not null,
  image_url text
);

-- جدول الكتب
create table if not exists books (
  isbn text primary key,
  title text not null,
  nb_pages integer not null,
  image_url text,
  is_finished boolean not null default false
);

-- ربط جدول الكتب بجدول التصنيفات
alter table books
  add column category_id text references categories(id) on delete set null;

-- أو إذا كنت تنشئ الجدول لأول مرة مع الربط
create table if not exists books (
  isbn text primary key,
  title text not null,
  nb_pages integer not null,
  image_url text,
  is_finished boolean not null default false,
  category_id text references categories(id) on delete set null
);
```

## بيانات تجريبية

```sql
insert into categories (id, name, description, image_url) values
('programming', 'Programming', 'كتب تعلم البرمجة وتطوير التطبيقات.', null),
('design', 'Design', 'كتب عن التصميم، هندسة البرمجيات، وأنماط التصميم.', null),
('productivity', 'Productivity', 'كتب عن الإنتاجية والعادات المنظمة.', null)
on conflict (id) do nothing;

insert into books (isbn, title, nb_pages, image_url, is_finished) values
('9780132350884', 'Clean Code', 464, 'https://covers.openlibrary.org/b/isbn/9780132350884-L.jpg', false),
('9780201616224', 'The Pragmatic Programmer', 352, 'https://covers.openlibrary.org/b/isbn/9780201616224-L.jpg', false),
('9780201633610', 'Design Patterns', 395, 'https://covers.openlibrary.org/b/isbn/9780201633610-L.jpg', false),
('9780201485677', 'Refactoring', 461, 'https://covers.openlibrary.org/b/isbn/9780201485677-L.jpg', false),
('9780596007126', 'Head First Design Patterns', 694, 'https://covers.openlibrary.org/b/isbn/9780596007126-L.jpg', false)
on conflict (isbn) do nothing;
```

## إعداد تخزين الصور

إذا كنت تستخدم أغطية كتب مرفوعة، يمكنك إنشاء bucket جديد في Supabase Storage باسم:

- `book_covers`

ثم تأكد من أن سياسات الوصول تسمح بالقراءة العامة إذا كنت تريد استخدام `publicUrl()` لعرض الصور من التطبيق.

## ملاحظة

هذا المخطط يتوافق مع الكيانات التالية في المشروع:

- `Book` (isbn, title, nbPages, imageUrl, isFinished)
- `Category` (id, name, description, imageUrl)

إذا أردت إضافة علاقة بين الكتب والتصنيفات لاحقًا، يمكن إضافة عمود `category_id` على جدول `books` مع مفتاح أجنبي إلى `categories(id)`.
